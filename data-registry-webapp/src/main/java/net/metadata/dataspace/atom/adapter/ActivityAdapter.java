package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Activity;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:24:08 PM
 */
public class ActivityAdapter extends AbstractEntityCollectionAdapter<Activity> {

    private Logger logger = Logger.getLogger(getClass());
    private ActivityDao activityDao = DataRegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private EntityCreator entityCreator = DataRegistryApplication.getApplicationContext().getEntityCreator();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        MimeType mimeType = request.getContentType();
        String baseType = mimeType.getBaseType();
        if (baseType.equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            try {
                Entry entry = getEntryFromRequest(request);
                Activity activity = entityCreator.getNextActivity();
                boolean isValidEntry = AdapterHelper.updateActivityFromEntry(activity, entry);
                if (!isValidEntry) {
                    return ProviderHelper.badrequest(request, "Invalid Entry");
                } else {
                    activityDao.save(activity);
                    Entry createdEntry = furtherUpdate(entry, activity);
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
                String jsonString = AdapterHelper.getJsonString(request.getInputStream());
                Activity activity = entityCreator.getNextActivity();
                assembleActivityFromJson(activity, jsonString);
                Entry createdEntry = AdapterHelper.getEntryFromActivity(activity);
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
                String uriKey = AdapterHelper.getEntityID(entry.getId().toString());
                Activity activity = activityDao.getByKey(uriKey);
                boolean isValidEntry = AdapterHelper.updateActivityFromEntry(activity, entry);
                if (activity == null || !isValidEntry) {
                    return ProviderHelper.badrequest(request, "Invalid Entry");
                } else {
                    if (activity.isActive()) {
                        activityDao.update(activity);
                        Entry createdEntry = furtherUpdate(entry, activity);
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
        logger.info("Updating Media Entry");
        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot get inputstream from request.", e);
                return ProviderHelper.servererror(request, e);
            }
            String activityAsJsonString = AdapterHelper.getJsonString(inputStream);
            String uriKey = AdapterHelper.getEntryID(request);
            Activity activity = activityDao.getByKey(uriKey);
            assembleActivityFromJson(activity, activityAsJsonString);
            activityDao.update(activity);
            Entry createdEntry = AdapterHelper.getEntryFromActivity(activity);
            return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
        } else {
            return ProviderHelper.notsupported(request, "Unsupported Media Type");
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        Activity activity = activityDao.getByKey(uriKey);
        if (activity == null) {
            return ProviderHelper.notfound(request);
        } else {
            activityDao.refresh(activity);
            if (activity.isActive()) {
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
        Activity activity = activityDao.getByKey(uriKey);
        if (activity == null) {
            return ProviderHelper.notfound(request);
        } else {
            activityDao.refresh(activity);
            if (activity.isActive()) {
                Entry entry = AdapterHelper.getEntryFromActivity(activity);
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
                return FeedHelper.getHtmlRepresentationOfFeed(request, "activity.jsp");
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
                return FeedHelper.getHtmlRepresentationOfFeed(request, "activity.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        Activity latestActivity = activityDao.getMostRecentUpdated();
        if (latestActivity != null) {
            activityDao.refresh(latestActivity);
            feed.setUpdated(latestActivity.getUpdated());
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
        String atomFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            FeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            FeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }

        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_ACTIVITIES);
        Iterable<Activity> entries = getEntries(request);
        if (entries != null) {
            for (Activity entryObj : entries) {
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
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_ENTRY_MIMETYPE, Constants.JSON_MIMETYPE};
    }

    @Override
    public Activity postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext request) throws ResponseContextException {
        activityDao.softDelete(key);
    }

    @Override
    public Object getContent(Activity entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public Iterable<Activity> getEntries(RequestContext request) throws ResponseContextException {
        return activityDao.getAllActive();
    }

    @Override
    public Activity getEntry(String key, RequestContext request) throws ResponseContextException {
        Activity activity = activityDao.getByKey(key);
        if (activity != null) {
            activityDao.refresh(activity);
        }
        return activity;
    }

    @Override
    public String getId(Activity entry) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Activity entry) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + entry.getUriKey();
    }

    @Override
    public String getTitle(Activity entry) throws ResponseContextException {
        return entry.getTitle();
    }

    @Override
    public Date getUpdated(Activity entry) throws ResponseContextException {
        return entry.getUpdated();
    }

    @Override
    public void putEntry(Activity entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext request) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_ACTIVITIES;
    }

    private Entry furtherUpdate(Entry entry, Activity activity) {
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_HAS_OUTPUT);
        for (String key : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(key);
            if (collection != null) {
                collection.getOutputOf().add(activity);
                activity.getHasOutput().add(collection);
            }
        }
        activityDao.update(activity);

        Set<String> partyUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_HAS_PARTICIPANT);
        for (String partyKey : partyUriKeys) {
            Party party = partyDao.getByKey(partyKey);
            if (party != null) {
                party.getParticipantIn().add(activity);
                activity.getHasParticipant().add(party);
            }
        }
        activity.setUpdated(new Date());
        activityDao.update(activity);

        Entry createdEntry = AdapterHelper.getEntryFromActivity(activity);
        return createdEntry;
    }

    private void assembleActivityFromJson(Activity activity, String activityAsJsonString) {
        try {
            JSONObject jsonObj = new JSONObject(activityAsJsonString);
            activity.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            activity.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            activity.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            activity.setUpdated(new Date());
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            activity.setAuthors(persons);

            if (activity.getId() == null) {
                activityDao.save(activity);
            }

            JSONArray collections = jsonObj.getJSONArray(Constants.ELEMENT_NAME_HAS_OUTPUT);
            for (int i = 0; i < collections.length(); i++) {
                Collection collection = collectionDao.getByKey(collections.getString(i));
                if (collection != null) {
                    collection.getOutputOf().add(activity);
                    activity.getHasOutput().add(collection);
                }
            }
            activityDao.update(activity);

            JSONArray parties = jsonObj.getJSONArray(Constants.ELEMENT_NAME_HAS_PARTICIPANT);
            for (int i = 0; i < parties.length(); i++) {
                Party party = partyDao.getByKey(parties.getString(i));
                if (party != null) {
                    party.getParticipantIn().add(activity);
                    activity.getHasParticipant().add(party);
                }
            }
            activityDao.update(activity);
        } catch (JSONException ex) {
            logger.fatal("Could not assemble party from JSON object", ex);
        }

    }

}
