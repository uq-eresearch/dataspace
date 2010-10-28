package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.util.AtomFeedHelper;
import net.metadata.dataspace.atom.util.CollectionAdapterHelper;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.Subject;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionAdapter extends AbstractEntityCollectionAdapter<Collection> {

    private Logger logger = Logger.getLogger(getClass());
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private SubjectDao subjectDao = DataRegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private EntityCreator entityCreator = DataRegistryApplication.getApplicationContext().getEntityCreator();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        MimeType mimeType = request.getContentType();
        String baseType = mimeType.getBaseType();
        if (baseType.equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            try {
                Entry entry = getEntryFromRequest(request);
                Collection collection = entityCreator.getNextCollection();
                boolean isValidColleciton = CollectionAdapterHelper.updateCollectionFromEntry(collection, entry);
                if (!isValidColleciton) {
                    return ProviderHelper.badrequest(request, "Invalid Entry");
                } else {
                    collectionDao.save(collection);

                    Set<Subject> subjects = CollectionAdapterHelper.getSubjects(entry);
                    for (Subject subject : subjects) {
                        collection.getSubjects().add(subject);
                        subjectDao.save(subject);
                    }
                    collectionDao.update(collection);

                    Set<String> collectorUriKeys = CollectionAdapterHelper.getUriKeysFromExtension(entry, Constants.COLLECTOR_QNAME);
                    for (String uriKey : collectorUriKeys) {
                        Party party = partyDao.getByKey(uriKey);
                        if (party != null) {
                            party.getCollectorOf().add(collection);
                            collection.getCollector().add(party);
                        }
                    }
                    collection.setUpdated(new Date());
                    collectionDao.update(collection);

                    Entry createdEntry = CollectionAdapterHelper.getEntryFromCollection(collection);
                    return ProviderHelper.returnBase(createdEntry, 201, createdEntry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(createdEntry));
                }
            } catch (ResponseContextException e) {
                logger.fatal("Invalid Entry", e);
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            try {
                String jsonString = CollectionAdapterHelper.getJsonString(request.getInputStream());
                Collection collection = entityCreator.getNextCollection();
                assembleCollectionFromJson(collection, jsonString);
                Entry createdEntry = CollectionAdapterHelper.getEntryFromCollection(collection);
                return ProviderHelper.returnBase(createdEntry, 201, createdEntry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(createdEntry));
            } catch (IOException e) {
                logger.fatal("Cannot get inputstream from request.");
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        logger.info("Updating Entry");
        String mimeBaseType = request.getContentType().getBaseType();
        if (mimeBaseType.equals(Constants.JSON_MIMETYPE)) {
            putMedia(request);
        } else if (mimeBaseType.equals(Constants.ATOM_MIMETYPE)) {
            try {
                Entry entry = getEntryFromRequest(request);
                String collectionUriKey = CollectionAdapterHelper.getEntityID(entry.getId().toString());
                Collection collection = collectionDao.getByKey(collectionUriKey);
                boolean isValidaCollection = CollectionAdapterHelper.updateCollectionFromEntry(collection, entry);
                if (collection == null || !isValidaCollection) {
                    return ProviderHelper.badrequest(request, "Invalid Entry");
                } else {
                    if (collection.isActive()) {
                        collectionDao.update(collection);
                        Set<Subject> subjects = CollectionAdapterHelper.getSubjects(entry);
                        collection.setSubjects(subjects);
                        for (Subject subject : subjects) {
                            subjectDao.save(subject);
                        }
                        collectionDao.update(collection);

                        Set<String> collectorUriKeys = CollectionAdapterHelper.getUriKeysFromExtension(entry, Constants.COLLECTOR_QNAME);
                        for (String uriKey : collectorUriKeys) {
                            Party party = partyDao.getByKey(uriKey);
                            if (party != null) {
                                party.getCollectorOf().add(collection);
                                collection.getCollector().add(party);
                            }
                        }
                        collection.setUpdated(new Date());
                        collectionDao.update(collection);

                        Entry createdEntry = CollectionAdapterHelper.getEntryFromCollection(collection);
                        return CollectionAdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                    } else {
                        return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
                    }
                }
            } catch (ResponseContextException e) {
                logger.fatal("Invalid Entry", e);
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
        return getEntry(request);
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        logger.info("Updating Media Entry");
        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
                return ProviderHelper.servererror(request, e);
            }
            String collectionAsJsonString = CollectionAdapterHelper.getJsonString(inputStream);
            String uriKey = CollectionAdapterHelper.getEntryID(request);
            Collection collection = collectionDao.getByKey(uriKey);
            assembleCollectionFromJson(collection, collectionAsJsonString);
            collectionDao.update(collection);
            Entry createdEntry = CollectionAdapterHelper.getEntryFromCollection(collection);
            return CollectionAdapterHelper.getContextResponseForGetEntry(request, createdEntry);
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Collection collection = collectionDao.getByKey(uriKey);
        if (collection == null) {
            return ProviderHelper.notfound(request);
        } else {
            collectionDao.refresh(collection);
            if (collection.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, "OK");
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete collection entry");
                    return ProviderHelper.servererror(request, e);
                }
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Collection collection = collectionDao.getByKey(uriKey);
        collectionDao.refresh(collection);
        if (collection == null) {
            return ProviderHelper.notfound(request);
        } else {
            if (collection.isActive()) {
                Entry entry = CollectionAdapterHelper.getEntryFromCollection(collection);
                return CollectionAdapterHelper.getContextResponseForGetEntry(request, entry);
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = AtomFeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return ProviderHelper.notsupported(request, "Unsupported Media Type");
            }
        } else {
            String accept = request.getAccept();
            if (accept.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        Collection latestCollection = collectionDao.getMostRecentUpdated();
        if (latestCollection != null) {
            collectionDao.refresh(latestCollection);
            feed.setUpdated(latestCollection.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = AtomFeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.HTML_MIME_TYPE) || acceptHeader.equals(Constants.ATOM_FEED_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.HTML_MIME_TYPE;
            }
        }
        String atomFeedUrl = ID_PREFIX + Constants.COLLECTIONS_PATH + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = ID_PREFIX + Constants.COLLECTIONS_PATH;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            AtomFeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            AtomFeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            AtomFeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            AtomFeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }
        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.COLLECTIONS_TITLE);
        Iterable<Collection> entries = getEntries(request);
        if (entries != null) {
            for (Collection entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    public List<Person> getAuthors(Collection collection, RequestContext request) throws ResponseContextException {
        Set<String> authors = collection.getAuthors();
        List<Person> personList = new ArrayList<Person>();
        for (String author : authors) {
            Person person = request.getAbdera().getFactory().newAuthor();
            person.setName(author);
            personList.add(person);
        }
        return personList;
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_ENTRY_MIMETYPE, Constants.JSON_MIMETYPE};
    }

    @Override
    public Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                Content content, RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        collectionDao.softDelete(key);
    }

    @Override
    public Object getContent(Collection collection, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(collection.getContent());
        return content;
    }

    @Override
    public Iterable<Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getAllActive();
    }

    @Override
    public Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        Collection collection = collectionDao.getByKey(key);
        if (collection != null) {
            collectionDao.refresh(collection);
        }
        return collection;
    }

    @Override
    public String getId(Collection collection) throws ResponseContextException {
        return ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey();
    }

    @Override
    public String getName(Collection collection) throws ResponseContextException {
        return ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey();
    }

    @Override
    public String getTitle(Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public Date getUpdated(Collection collection) throws ResponseContextException {
        return collection.getUpdated();
    }

    @Override
    public void putEntry(Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return ID_PREFIX + Constants.COLLECTIONS_PATH;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.COLLECTIONS_TITLE;
    }

    private void assembleCollectionFromJson(Collection collection, String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            collection.setTitle(jsonObj.getString("title"));
            collection.setSummary(jsonObj.getString("summary"));
            collection.setContent(jsonObj.getString("content"));
            collection.setUpdated(new Date());
            collection.setLocation(jsonObj.getString("location"));
            JSONArray authors = jsonObj.getJSONArray("authors");
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            collection.setAuthors(persons);

            if (collection.getId() == null) {
                collectionDao.save(collection);
            }

            JSONArray subjectArray = jsonObj.getJSONArray("subject");
            for (int i = 0; i < subjectArray.length(); i++) {
                Subject subject = entityCreator.getNextSubject();
                subject.setVocabulary(subjectArray.getJSONObject(i).getString("vocabulary"));
                subject.setValue(subjectArray.getJSONObject(i).getString("value"));
                collection.getSubjects().add(subject);
                subjectDao.save(subject);
            }
            collectionDao.update(collection);
            JSONArray collectors = jsonObj.getJSONArray("collector");
            for (int i = 0; i < collectors.length(); i++) {
                Party party = partyDao.getByKey(collectors.getString(i));
                if (party != null) {
                    party.getCollectorOf().add(collection);
                    collection.getCollector().add(party);
                }
            }
            collectionDao.update(collection);
        } catch (JSONException ex) {
            logger.fatal("Could not assemble collection from JSON object", ex);
        }
    }
}
