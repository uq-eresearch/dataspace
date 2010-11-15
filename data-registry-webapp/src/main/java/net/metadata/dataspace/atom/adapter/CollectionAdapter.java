package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.base.Service;
import net.metadata.dataspace.data.model.base.Subject;
import net.metadata.dataspace.data.model.version.CollectionVersion;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionAdapter extends AbstractEntityCollectionAdapter<net.metadata.dataspace.data.model.base.Collection> {

    private Logger logger = Logger.getLogger(getClass());
    private CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private PartyDao partyDao = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private SubjectDao subjectDao = RegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        return HttpMethodHelper.postEntry(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        return HttpMethodHelper.postMedia(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        logger.info("Updating Entry");
        String mimeBaseType = request.getContentType().getBaseType();
        if (mimeBaseType.equals(Constants.JSON_MIMETYPE)) {
            putMedia(request);
        } else if (mimeBaseType.equals(Constants.ATOM_MIMETYPE)) {
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                String uriKey = AdapterHelper.getEntryID(request);
                net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
                if (collection == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (collection.isActive()) {
                        CollectionVersion collectionVersion = entityCreator.getNextCollectionVersion(collection);
                        boolean isValidaEntry = AdapterHelper.isValidVersionFromEntry(collectionVersion, entry);
                        if (!isValidaEntry) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        } else {
                            transaction.begin();
                            collection.getVersions().add(collectionVersion);
                            collectionVersion.setParent(collection);
                            furtherUpdate(entry, collectionVersion);
                            entityManager.merge(collection);
                            transaction.commit();
                            Entry createdEntry = AdapterHelper.getEntryFromCollection(collectionVersion, false);
                            return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                        }
                    } else {
                        return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
                    }
                }
            } catch (Exception e) {
                logger.fatal("Invalid Entry", e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
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
            String collectionAsJsonString = AdapterHelper.getJsonString(inputStream);
            if (collectionAsJsonString == null) {
                return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
            } else {
                String uriKey = AdapterHelper.getEntryID(request);
                net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
                if (collection == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (collection.isActive()) {
                        CollectionVersion collectionVersion = entityCreator.getNextCollectionVersion(collection);
                        if (!assembleCollectionFromJson(collection, collectionVersion, collectionAsJsonString)) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        }
                        Entry createdEntry = AdapterHelper.getEntryFromCollection(collectionVersion, false);
                        return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                    } else {
                        return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
                    }
                }
            }
        } else {
            return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
        if (collection == null) {
            return ProviderHelper.notfound(request);
        } else {
            collectionDao.refresh(collection);
            if (collection.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete collection entry");
                    return ProviderHelper.servererror(request, e);
                }
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
            }
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
        if (collection == null) {
            return ProviderHelper.notfound(request);
        } else {
            collectionDao.refresh(collection);
            if (collection.isActive()) {
                String versionKey = AdapterHelper.getEntryVersionID(request);
                CollectionVersion collectionVersion;
                if (versionKey != null) {
                    collectionVersion = collectionDao.getByVersion(uriKey, versionKey);
                } else {
                    collectionVersion = collection.getVersions().first();
                }
                Entry entry = AdapterHelper.getEntryFromCollection(collectionVersion, versionKey == null);
                return AdapterHelper.getContextResponseForGetEntry(request, entry);
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
            }
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
            }
        } else {
            String accept = request.getAccept();
            if (accept.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        net.metadata.dataspace.data.model.base.Collection latestCollection = collectionDao.getMostRecentUpdated();
        if (latestCollection != null) {
            collectionDao.refresh(latestCollection);
            feed.setUpdated(latestCollection.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.HTML_MIME_TYPE) || acceptHeader.equals(Constants.ATOM_FEED_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.HTML_MIME_TYPE;
            }
        }
        String atomFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            FeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            FeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }
        feed.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_COLLECTIONS);
        Iterable<net.metadata.dataspace.data.model.base.Collection> entries = getEntries(request);
        if (entries != null) {
            for (net.metadata.dataspace.data.model.base.Collection entryObj : entries) {
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

    public List<Person> getAuthors(net.metadata.dataspace.data.model.base.Collection collection, RequestContext request) throws ResponseContextException {
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
    public net.metadata.dataspace.data.model.base.Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                                                       Content content, RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        collectionDao.softDelete(key);
    }

    @Override
    public Object getContent(net.metadata.dataspace.data.model.base.Collection collection, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(collection.getContent());
        return content;
    }

    @Override
    public Iterable<net.metadata.dataspace.data.model.base.Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getAllActive();
    }

    @Override
    public net.metadata.dataspace.data.model.base.Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(key);
        if (collection != null) {
            collectionDao.refresh(collection);
        }
        return collection;
    }

    @Override
    public String getId(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    public String getName(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    public String getTitle(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public Date getUpdated(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return collection.getUpdated();
    }

    @Override
    public void putEntry(net.metadata.dataspace.data.model.base.Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_COLLECTIONS;
    }

    private void furtherUpdate(Entry entry, CollectionVersion collectionVersion) {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            collectionVersion.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectorUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_COLLECTOR);
        for (String uriKey : collectorUriKeys) {
            Party party = partyDao.getByKey(uriKey);
            if (party != null) {
                party.getCollectorOf().add((net.metadata.dataspace.data.model.base.Collection) collectionVersion.getParent());
                collectionVersion.getCollector().add(party);
                entityManager.merge(party);
            }
        }
        Set<String> outputOfUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_IS_OUTPUT_OF);
        for (String uriKey : outputOfUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasOutput().add((net.metadata.dataspace.data.model.base.Collection) collectionVersion.getParent());
                collectionVersion.getOutputOf().add(activity);
                entityManager.merge(activity);
            }
        }
        Set<String> supportUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_SUPPORTS);
        for (String uriKey : supportUriKeys) {
            Service service = serviceDao.getByKey(uriKey);
            if (service != null) {
                service.getSupportedBy().add((net.metadata.dataspace.data.model.base.Collection) collectionVersion.getParent());
                collectionVersion.getSupports().add(service);
                entityManager.merge(service);
            }
        }
        Date now = new Date();
        collectionVersion.setUpdated(now);
        collectionVersion.getParent().setUpdated(now);
    }

    private boolean assembleCollectionFromJson(net.metadata.dataspace.data.model.base.Collection collection, CollectionVersion collectionVersion, String jsonString) {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(jsonString);
            collectionVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            collectionVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            collectionVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            Date now = new Date();
            collectionVersion.setUpdated(now);
            collection.setUpdated(now);
            collectionVersion.setLocation(jsonObj.getString(Constants.ELEMENT_NAME_LOCATION));
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            collectionVersion.setAuthors(persons);
            if (collection.getId() == null) {
                entityManager.persist(collection);
            }
            JSONArray collectors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_COLLECTOR);
            for (int i = 0; i < collectors.length(); i++) {
                Party party = partyDao.getByKey(collectors.getString(i));
                if (party != null) {
                    party.getCollectorOf().add(collection);
                    collectionVersion.getCollector().add(party);
                    entityManager.merge(party);
                }
            }
            JSONArray subjectArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUBJECT);
            for (int i = 0; i < subjectArray.length(); i++) {
                String vocabulary = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VOCABULARY);
                String value = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VALUE);
                if (vocabulary == null || value == null) {
                    return false;
                } else {
                    Subject subject = subjectDao.getSubject(vocabulary, value);
                    if (subject == null) {
                        subject = entityCreator.getNextSubject();
                        subject.setVocabulary(vocabulary);
                        subject.setValue(value);
                        entityManager.persist(subject);
                    }
                    collectionVersion.getSubjects().add(subject);
                    entityManager.merge(subject);
                }
            }
            collection.getVersions().add(collectionVersion);
            collectionVersion.setParent(collection);
            entityManager.merge(collection);
            transaction.commit();
        } catch (JSONException ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }
}
