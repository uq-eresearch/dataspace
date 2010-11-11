package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.*;
import net.metadata.dataspace.data.model.Collection;
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
import org.json.JSONObject;

import javax.activation.MimeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
    private ActivityDao activityDao = DataRegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private SubjectDao subjectDao = DataRegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private AuthenticationManager authManager = DataRegistryApplication.getApplicationContext().getAuthenticationManager();

    @Override
    public ResponseContext postEntry(RequestContext request) {
//        User user = authManager.getCurrentUser(request);
//        if (user == null) {
//            return ProviderHelper.unauthorized(request, Constants.HTTP_STATUS_401);
//        } else {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            EntityManager entityManager = DataRegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                Party party = entityCreator.getNextParty();
                PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
                boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(partyVersion, entry);
                if (!isValidEntry) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    transaction.begin();
                    partyVersion.setParent(party);
                    party.getVersions().add(partyVersion);
                    Date now = new Date();
                    partyVersion.setUpdated(now);
                    party.setUpdated(now);
                    entityManager.persist(partyVersion);
                    entityManager.persist(party);
                    furtherUpdate(entry, partyVersion);
                    transaction.commit();
                    Entry createdEntry = AdapterHelper.getEntryFromParty(partyVersion, true);
                    return AdapterHelper.getContextResponseForPost(createdEntry);
                }
            } catch (Exception e) {
                logger.warn("Invalid Entry", e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, "Unsupported media type");
        }
//        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            try {
                String jsonString = AdapterHelper.getJsonString(request.getInputStream());
                if (jsonString == null) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    Party party = entityCreator.getNextParty();
                    PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
                    if (!assembleValidPartyFromJson(party, partyVersion, jsonString)) {
                        return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                    } else {
                    }
                    Entry createdEntry = AdapterHelper.getEntryFromParty(partyVersion, true);
                    return AdapterHelper.getContextResponseForPost(createdEntry);
                }
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
            EntityManager entityManager = DataRegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                String uriKey = AdapterHelper.getEntryID(request);
                Party party = partyDao.getByKey(uriKey);
                if (party == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (party.isActive()) {
                        PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
                        boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(partyVersion, entry);
                        if (!isValidEntry) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        } else {
                            transaction.begin();
                            party.getVersions().add(partyVersion);
                            partyVersion.setParent(party);
                            furtherUpdate(entry, partyVersion);
                            entityManager.merge(party);
                            transaction.commit();
                            Entry createdEntry = AdapterHelper.getEntryFromParty(partyVersion, false);
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
        logger.info("Updating Party as Media Entry");

        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
            }
            String partyAsJsonString = AdapterHelper.getJsonString(inputStream);
            if (partyAsJsonString == null) {
                return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
            } else {
                String uriKey = AdapterHelper.getEntryID(request);
                Party party = partyDao.getByKey(uriKey);
                if (party == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (party.isActive()) {
                        PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
                        if (!assembleValidPartyFromJson(party, partyVersion, partyAsJsonString)) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        }
                        Entry createdEntry = AdapterHelper.getEntryFromParty(partyVersion, false);
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
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            partyDao.refresh(party);
            if (party.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete party entry");
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
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            partyDao.refresh(party);
            if (party.isActive()) {
                String versionKey = AdapterHelper.getEntryVersionID(request);
                PartyVersion partyVersion;
                if (versionKey != null) {
                    partyVersion = partyDao.getByVersion(uriKey, versionKey);
                } else {
                    partyVersion = party.getVersions().first();
                }
                Entry entry = AdapterHelper.getEntryFromParty(partyVersion, versionKey == null);
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
                return FeedHelper.getHtmlRepresentationOfFeed(request, "party.jsp");
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

    private void furtherUpdate(Entry entry, PartyVersion partyVersion) {
        EntityManager entityManager = DataRegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            partyVersion.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_COLLECTOR_OF);
        for (String uriKey : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getCollector().add(partyVersion.getParent());
                partyVersion.getCollectorOf().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> isParticipantInUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_IS_PARTICIPANT_IN);
        for (String uriKey : isParticipantInUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasParticipant().add(partyVersion.getParent());
                partyVersion.getParticipantIn().add(activity);
                entityManager.merge(activity);
            }
        }
        Date now = new Date();
        partyVersion.setUpdated(now);
        partyVersion.getParent().setUpdated(now);
    }

    private boolean assembleValidPartyFromJson(Party party, PartyVersion partyVersion, String jsonString) {
        EntityManager entityManager = DataRegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(jsonString);
            partyVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            partyVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            partyVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            Date now = new Date();
            partyVersion.setUpdated(now);
            party.setUpdated(now);
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            partyVersion.setAuthors(persons);

            if (party.getId() == null) {
                entityManager.persist(party);
            }
            if (partyVersion.getId() == null) {
                entityManager.persist(partyVersion);
            }

            JSONArray collectionArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_COLLECTOR_OF);
            for (int i = 0; i < collectionArray.length(); i++) {
                Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getCollector().add(party);
                    partyVersion.getCollectorOf().add(collection);
                    entityManager.merge(collection);
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
                    partyVersion.getSubjects().add(subject);
                    entityManager.merge(subject);
                }
            }
            party.getVersions().add(partyVersion);
            partyVersion.setParent(party);
            entityManager.merge(party);
            transaction.commit();
        } catch (Exception ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }
}
