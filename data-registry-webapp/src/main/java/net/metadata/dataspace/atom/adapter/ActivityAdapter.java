package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.Activity;
import net.metadata.dataspace.util.AtomFeedHelper;
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
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();
    private ActivityDao activityDao = DataRegistryApplication.getApplicationContext().getDaoManager().getActivityDao();


    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = AtomFeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "activity.jsp");
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
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "activity.jsp");
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
            String selfLinkHref = ID_PREFIX + Constants.ACTIVITIES_PATH;
            AtomFeedHelper.prepareFeedSelfLink(feed, selfLinkHref, Constants.HTML_MIME_TYPE);

            String alternateLinkHref = ID_PREFIX + Constants.ACTIVITIES_PATH + "?repr=application/atom+xml;type=feed";
            AtomFeedHelper.prepareFeedAlternateLink(feed, alternateLinkHref, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            String alternateLinkHref = ID_PREFIX + Constants.ACTIVITIES_PATH + "?repr=application/atom+xml;type=feed";
            AtomFeedHelper.prepareFeedSelfLink(feed, alternateLinkHref, Constants.ATOM_FEED_MIMETYPE);

            String selfLinkHref = ID_PREFIX + Constants.ACTIVITIES_PATH;
            AtomFeedHelper.prepareFeedAlternateLink(feed, selfLinkHref, Constants.HTML_MIME_TYPE);
        }

        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.ACTIVITIES_TITLE);
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
        return ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Activity entry) throws ResponseContextException {
        return ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + entry.getUriKey();
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
        return ID_PREFIX + Constants.ACTIVITIES_PATH;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.ACTIVITIES_TITLE;
    }
}
