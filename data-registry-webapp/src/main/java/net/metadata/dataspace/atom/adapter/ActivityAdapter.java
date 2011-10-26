package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.record.Activity;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:24:08 PM
 */
@Transactional
public class ActivityAdapter extends AbstractRecordAdapter<Activity> {

	@Override
	protected Class<Activity> getRecordClass() {
		return Activity.class;
	}

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        getHttpMethodHelper().addFeedDetails(feed, request, getRecordClass());
        Iterable<Activity> entries = getEntries(request);
        if (entries != null) {
            for (Activity entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                getFeedOutputHelper().setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                    Link typeLink = e.addLink(Constants.TERM_ACTIVITY, Constants.REL_TYPE);
                    typeLink.setTitle("Activity");
                }
            }
        }
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
        getDao().softDelete(key);
    }

    @Override
    public Object getContent(Activity entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public Iterable<Activity> getEntries(RequestContext requestContext) throws ResponseContextException {
        return getRecords(requestContext);
    }

    @Override
    public Activity getEntry(String key, RequestContext request) throws ResponseContextException {
        Activity activity = getDao().getByKey(key);
        if (activity != null) {
            getDao().refresh(activity);
        }
        return activity;
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
        return Constants.UQ_REGISTRY_URI_PREFIX + getBasePath();
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_ACTIVITIES;
    }

	@Override
	protected String getBasePath() {
		return Constants.PATH_FOR_ACTIVITIES;
	}
}
