package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.record.Activity;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:24:08 PM
 */
@Transactional
public class ActivityAdapter extends AbstractEntityCollectionAdapter<Activity> {

    private Logger logger = Logger.getLogger(getClass());
    private ActivityDao activityDao;
        
    public ActivityDao getActivityDao() {
		return activityDao;
	}

	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

    @Override
    public ResponseContext postEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().postEntry(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().postMedia(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().putEntry(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        try {
            return HttpMethodHelper.putMedia(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().deleteEntry(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().getEntry(request, Activity.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        try {
            String representationMimeType = FeedOutputHelper.getRepresentationMimeType(request);
            String accept = request.getAccept();
            if (representationMimeType == null) {
                representationMimeType = accept;
            }
            if (representationMimeType != null &&
                    (representationMimeType.equals(Constants.MIME_TYPE_ATOM_FEED) ||
                            representationMimeType.equals(Constants.MIME_TYPE_ATOM))) {
                return super.getFeed(request);
            } else {
                Feed feed = createFeedBase(request);
                addFeedDetails(feed, request);
                ResponseContext responseContext = buildGetFeedResponse(feed);

                return HttpMethodHelper.getFeed(request, responseContext, Activity.class);
            }
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        HttpMethodHelper.getInstance().addFeedDetails(feed, request, Activity.class);
        Iterable<Activity> entries = getEntries(request);
        if (entries != null) {
            for (Activity entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                FeedOutputHelper.setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    public List<Person> getAuthors(Activity activity, RequestContext request) throws ResponseContextException {
        return HttpMethodHelper.getInstance().getAuthors(activity, request);
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
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
    public Iterable<Activity> getEntries(RequestContext requestContext) throws ResponseContextException {
        return HttpMethodHelper.getInstance().getRecords(requestContext, Activity.class);
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
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Activity entry) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + entry.getUriKey();
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
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_ACTIVITIES;
    }
}
