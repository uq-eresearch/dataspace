package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.Subject;
import net.metadata.dataspace.util.AtomFeedHelper;
import net.metadata.dataspace.util.CollectionAdapterHelper;
import net.metadata.dataspace.util.DaoHelper;
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
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyCollectionAdapter extends AbstractEntityCollectionAdapter<Party> {

    private Logger logger = Logger.getLogger(getClass());
    private final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getDaoRegister().getCollectionDao();
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getDaoRegister().getPartyDao();
    private SubjectDao subjectDao = DataRegistryApplication.getApplicationContext().getDaoRegister().getSubjectDao();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            try {
                Entry entry = getEntryFromRequest(request);
                Party party = DaoHelper.getNextParty();
                boolean isValidParty = CollectionAdapterHelper.updatePartyFromEntry(party, entry);
                if (!isValidParty) {
                    return ProviderHelper.badrequest(request, "Invalid entry posted.");
                } else {
                    partyDao.save(party);
                    Set<Subject> subjects = CollectionAdapterHelper.getSubjects(entry);
                    for (Subject subject : subjects) {
                        party.getSubjects().add(subject);
                        subjectDao.save(subject);
                    }
                    partyDao.update(party);


                    Set<String> collectionUriKeys = CollectionAdapterHelper.getCollectorUriKeys(entry);
                    for (String uriKey : collectionUriKeys) {
                        Collection collection = collectionDao.getByKey(uriKey);
                        if (collection != null) {
                            collection.getCollector().add(party);
                            party.getCollectorOf().add(collection);
                        }
                    }
                    party.setUpdated(new Date());
                    partyDao.update(party);

                    Entry createdEntry = CollectionAdapterHelper.getEntryFromParty(party);
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
                String partyAsJsonString = CollectionAdapterHelper.getJsonString(request.getInputStream());
                Party party = DaoHelper.getNextParty();
                assembleParty(party, partyAsJsonString);
                Entry createdEntry = CollectionAdapterHelper.getEntryFromParty(party);
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
        logger.info("Updating Party as Media Entry");

        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
            }
            String partyAsJsonString = CollectionAdapterHelper.getJsonString(inputStream);
            String uriKey = CollectionAdapterHelper.getEntryID(request);
            Party party = partyDao.getByKey(uriKey);
            assembleParty(party, partyAsJsonString);
            partyDao.update(party);
        }
        return getEntry(request);
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
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

    public ResponseContext getEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            partyDao.refresh(party);
            if (party.isActive()) {
                Entry entry = CollectionAdapterHelper.getEntryFromParty(party);
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
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "party.jsp");
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
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "party.jsp");
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

        String representationMimeType = AtomFeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.HTML_MIME_TYPE) || acceptHeader.equals(Constants.ATOM_FEED_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.HTML_MIME_TYPE;
            }
        }
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            String selfLinkHref = ID_PREFIX + "parties";
            AtomFeedHelper.prepareFeedSelfLink(feed, selfLinkHref, Constants.HTML_MIME_TYPE);

            String alternateLinkHref = ID_PREFIX + "parties?repr=application/atom+xml;type=feed";
            AtomFeedHelper.prepareFeedAlternateLink(feed, alternateLinkHref, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            String alternateLinkHref = ID_PREFIX + "parties?repr=application/atom+xml;type=feed";
            AtomFeedHelper.prepareFeedSelfLink(feed, alternateLinkHref, Constants.ATOM_FEED_MIMETYPE);

            String selfLinkHref = ID_PREFIX + "parties";
            AtomFeedHelper.prepareFeedAlternateLink(feed, selfLinkHref, Constants.HTML_MIME_TYPE);
        }

        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": Parties");
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

    @Override
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        Party party = DaoHelper.getNextParty();
        return party;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        partyDao.softDelete(key);
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
    public Iterable<Party> getEntries(RequestContext requestContext) throws ResponseContextException {
        return partyDao.getAllActive();
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
    public Object getContent(Party party, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(party.getSummary());
        return content;
    }

    @Override
    public String getId(Party party) throws ResponseContextException {
        return ID_PREFIX + "parties/" + party.getUriKey();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        //TODO this sets the link element which contains the edit link
        return ID_PREFIX + "parties/" + party.getUriKey();
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
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return ID_PREFIX + "parties";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Parties";
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_ENTRY_MIMETYPE, Constants.JSON_MIMETYPE};
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported");
    }

    private Set<String> getAuthors(List<Person> persons) {
        Set<String> authors = new HashSet<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }

    private void assembleParty(Party party, String jsonString) {

        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            party.setTitle(jsonObj.getString("title"));
            party.setSummary(jsonObj.getString("summary"));
            party.setContent(jsonObj.getString("content"));
            party.setUpdated(new Date());

            JSONArray authors = jsonObj.getJSONArray("authors");
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            party.setAuthors(persons);

            if (party.getId() == null) {
                partyDao.save(party);
            }

            JSONArray subjectArray = jsonObj.getJSONArray("subject");
            for (int i = 0; i < subjectArray.length(); i++) {
                Subject subject = DaoHelper.getNextSubject();
                subject.setVocabulary(subjectArray.getJSONObject(i).getString("vocabulary"));
                subject.setValue(subjectArray.getJSONObject(i).getString("value"));
                party.getSubjects().add(subject);
                subjectDao.save(subject);
            }
            partyDao.update(party);
            JSONArray collectionArray = jsonObj.getJSONArray("collectorof");
            for (int i = 0; i < collectionArray.length(); i++) {
                Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getCollector().add(party);
                    party.getCollectorOf().add(collection);
                }
            }
            partyDao.update(party);
        } catch (JSONException ex) {
            logger.fatal("Could not assemble party from JSON object", ex);
        }
    }

}
