package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.util.FeedHelper;
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
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyAdapter extends AbstractEntityCollectionAdapter<Party> {

    private Logger logger = Logger.getLogger(getClass());
    private EntityCreator entityCreator = DataRegistryApplication.getApplicationContext().getEntityCreator();
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private SubjectDao subjectDao = DataRegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private EntityManager enityManager = DataRegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            try {
                Entry entry = getEntryFromRequest(request);
                Party party = entityCreator.getNextParty();
                boolean isValidParty = AdapterHelper.updatePartyFromEntry(party, entry);
                if (!isValidParty) {
                    return ProviderHelper.badrequest(request, "Invalid entry posted.");
                } else {
                    enityManager.getTransaction().begin();
                    enityManager.persist(party);
                    Entry createdEntry = furtherUpdate(entry, party);
                    enityManager.getTransaction().commit();
                    return ProviderHelper.returnBase(createdEntry, 201, createdEntry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(createdEntry));
                }
            } catch (ResponseContextException e) {
                logger.fatal("Invalid entry posted.", e);
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported media type");
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            try {
                String partyAsJsonString = AdapterHelper.getJsonString(request.getInputStream());
                Party party = entityCreator.getNextParty();
                enityManager.getTransaction().begin();
                if (!assembleValidPartyFromJson(party, partyAsJsonString)) {
                    return ProviderHelper.badrequest(request, "Invalid entry posted.");
                }
                enityManager.getTransaction().commit();
                Entry createdEntry = AdapterHelper.getEntryFromParty(party);
                return ProviderHelper.returnBase(createdEntry, 201, createdEntry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(createdEntry));
            } catch (IOException e) {
                logger.fatal("Cannot get inputstream from request.");
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported media type");
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
                String uriKey = AdapterHelper.getEntityID(entry.getId().toString());
                Party party = partyDao.getByKey(uriKey);
                boolean isValidEntry = AdapterHelper.updatePartyFromEntry(party, entry);
                if (party == null || !isValidEntry) {
                    return ProviderHelper.badrequest(request, "Invalid Entry");
                } else {
                    if (party.isActive()) {
                        partyDao.update(party);
                        Entry createdEntry = furtherUpdate(entry, party);
                        return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
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
        logger.info("Updating Party as Media Entry");

        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
            }
            String partyAsJsonString = AdapterHelper.getJsonString(inputStream);
            String uriKey = AdapterHelper.getEntryID(request);
            Party party = partyDao.getByKey(uriKey);
            if (!assembleValidPartyFromJson(party, partyAsJsonString)) {
                return ProviderHelper.badrequest(request, "Invalid entry posted.");
            }
            partyDao.update(party);
            Entry createdEntry = AdapterHelper.getEntryFromParty(party);
            return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            partyDao.refresh(party);
            if (party.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, "OK");
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete party entry");
                    return ProviderHelper.servererror(request, e);
                }
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            partyDao.refresh(party);
            if (party.isActive()) {
                Entry entry = AdapterHelper.getEntryFromParty(party);
                return AdapterHelper.getContextResponseForGetEntry(request, entry);
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "party.jsp");
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
                return FeedHelper.getHtmlRepresentationOfFeed(request, "party.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        Party latestParty = partyDao.getMostRecentUpdated();
        if (latestParty != null) {
            partyDao.refresh(latestParty);
            feed.setUpdated(latestParty.getUpdated());
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
        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_PARTIES);
        Iterable<Party> entries = getEntries(request);
        if (entries != null) {
            for (Party entryObj : entries) {
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

    public List<Person> getAuthors(Party party, RequestContext request) throws ResponseContextException {
        Set<String> authors = party.getAuthors();
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
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        partyDao.softDelete(key);
    }

    @Override
    public Object getContent(Party party, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(party.getContent());
        return content;
    }

    @Override
    public Iterable<Party> getEntries(RequestContext requestContext) throws ResponseContextException {
        return partyDao.getAllActive();
    }

    @Override
    public Party getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        Party party = partyDao.getByKey(key);
        if (party != null) {
            partyDao.refresh(party);
        }
        return party;
    }

    @Override
    public String getId(Party party) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + party.getUriKey();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        //TODO this sets the link element which contains the edit link
        return Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + party.getUriKey();
    }

    @Override
    public String getTitle(Party party) throws ResponseContextException {
        return party.getTitle();
    }

    @Override
    public Date getUpdated(Party party) throws ResponseContextException {
        return party.getUpdated();
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported");
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_PARTIES;
    }

    private Entry furtherUpdate(Entry entry, Party party) {
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            party.getSubjects().add(subject);
            enityManager.persist(subject);
        }
        enityManager.merge(party);

        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_COLLECTOR_OF);
        for (String uriKey : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getCollector().add(party);
                party.getCollectorOf().add(collection);
            }
        }
        party.setUpdated(new Date());
        enityManager.merge(party);

        Entry createdEntry = AdapterHelper.getEntryFromParty(party);
        return createdEntry;
    }

    private boolean assembleValidPartyFromJson(Party party, String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            party.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            party.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            party.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            party.setUpdated(new Date());
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            party.setAuthors(persons);

            if (party.getId() == null) {
                enityManager.persist(party);
            }

            JSONArray collectionArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_COLLECTOR_OF);
            for (int i = 0; i < collectionArray.length(); i++) {
                Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getCollector().add(party);
                    party.getCollectorOf().add(collection);
                    enityManager.merge(collection);
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
                    }
                    party.getSubjects().add(subject);
                    enityManager.persist(subject);
                }
            }
            enityManager.merge(party);

        } catch (JSONException ex) {
            logger.fatal("Could not assemble party from JSON object", ex);
            return false;
        }
        return true;
    }

    private boolean isValidateJsonParty(String jsonString) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            return false;
        }
        return jsonObj.has(Constants.ELEMENT_NAME_TITLE)
                && jsonObj.has(Constants.ELEMENT_NAME_SUMMARY)
                && jsonObj.has(Constants.ELEMENT_NAME_CONTENT);
    }
}
