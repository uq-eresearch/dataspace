package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.base.Activity;
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

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:24:08 PM
 */
public class ActivityAdapter extends AbstractEntityCollectionAdapter<Activity> {

    private Logger logger = Logger.getLogger(getClass());
    private ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        return HttpMethodHelper.postEntry(request, Activity.class);
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        return HttpMethodHelper.postMedia(request, Activity.class);
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        return HttpMethodHelper.putEntry(request, Activity.class);
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        return HttpMethodHelper.putMedia(request, Activity.class);
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        return HttpMethodHelper.deleteEntry(request, Activity.class);
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        return HttpMethodHelper.getEntry(request, Activity.class);
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
                return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
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

        feed.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_ACTIVITIES);
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
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext request) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_ACTIVITIES;
    }
}
