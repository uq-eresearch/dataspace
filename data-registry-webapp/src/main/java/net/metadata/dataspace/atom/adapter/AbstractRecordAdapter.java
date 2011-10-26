package net.metadata.dataspace.atom.adapter;

import java.util.Date;
import java.util.List;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.RegistryDao;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.User;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractRecordAdapter<R extends Record<?>> extends
		AbstractEntityCollectionAdapter<R> {

	protected Logger logger = Logger.getLogger(getClass());

	private AuthenticationManager authenticationManager;
	private AuthorizationManager<User> authorizationManager;
	private HttpMethodHelper httpMethodHelper;
	private FeedOutputHelper feedOutputHelper;
    private RegistryDao<R> dao;

    public RegistryDao<R> getDao() {
		return dao;
	}

	public void setDao(RegistryDao<R> dao) {
		this.dao = dao;
	}

	public AbstractRecordAdapter() {
		super();
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public AuthorizationManager<User> getAuthorizationManager() {
		return authorizationManager;
	}

	@Transactional(readOnly=true)
    public List<Person> getAuthors(R record, RequestContext request) throws ResponseContextException {
        return getFeedOutputHelper().getAuthors(record, request);
    }

	public FeedOutputHelper getFeedOutputHelper() {
		return feedOutputHelper;
	}

	public HttpMethodHelper getHttpMethodHelper() {
		return httpMethodHelper;
	}

	public List<R> getRecords(RequestContext request) {
        User user = getAuthenticationManager().getCurrentUser(request);
        List<R> list;
        if (getAuthorizationManager().canAccessWorkingCopy(user, Collection.class)) {
            list = getDao().getAllPublished();
            list.addAll(getDao().getAllUnpublished());
        } else {
        	list = getDao().getAllPublished();
        }
        return list;
    }

	@Required
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Required
	public void setAuthorizationManager(AuthorizationManager<User> authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	@Required
	public void setFeedOutputHelper(FeedOutputHelper feedOutputHelper) {
		this.feedOutputHelper = feedOutputHelper;
	}

	@Required
	public void setHttpMethodHelper(HttpMethodHelper httpMethodHelper) {
		this.httpMethodHelper = httpMethodHelper;
	}

    @Override
    @Transactional(readOnly=true)
    public ResponseContext getFeed(RequestContext request) {
        try {
            String representationMimeType = getFeedOutputHelper().getRepresentationMimeType(request);
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
                return getFeed(request, responseContext);
            }
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

	protected ResponseContext getFeed(RequestContext request, ResponseContext responseContext) throws ResponseContextException {
		String representationMimeType = getFeedOutputHelper().getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.MIME_TYPE_HTML)) {
                return getFeedOutputHelper().getHtmlRepresentationOfFeed(request, responseContext, getRecordClass());
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        } else {
            return getFeedOutputHelper().getHtmlRepresentationOfFeed(request, responseContext, getRecordClass());
        }
    }

	protected abstract Class<R> getRecordClass();
	protected abstract String getLinkTerm();
	protected abstract String getBasePath();
	protected abstract String getTitle();

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ResponseContext postMedia(RequestContext request) {
	    try {
	        return getHttpMethodHelper().postMedia(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ResponseContext putEntry(RequestContext request) {
	    try {
	        return getHttpMethodHelper().putEntry(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ResponseContext putMedia(RequestContext request) {
	    try {
	        return getHttpMethodHelper().putMedia(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public ResponseContext deleteEntry(RequestContext request) {
	    try {
	        return getHttpMethodHelper().deleteEntry(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
	@Transactional(readOnly=true)
	public ResponseContext getEntry(RequestContext request) {
	    try {
	        return getHttpMethodHelper().getEntry(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ResponseContext postEntry(RequestContext request) {
	    try {
	        return getHttpMethodHelper().postEntry(request, getRecordClass());
	    } catch (ResponseContextException e) {
	        return OperationHelper.createErrorResponse(e);
	    }
	}

	@Override
    public String getId(R record) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + getBasePath() + "/" + record.getUriKey();
    }

    @Override
    public String getName(R record) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + getBasePath() + "/" + record.getUriKey();
    }

    @Override
    public String getTitle(R record) throws ResponseContextException {
        return record.getTitle();
    }

    @Override
    public Date getUpdated(R record) throws ResponseContextException {
        return record.getUpdated();
    }

    @Override
    public R getEntry(String key, RequestContext request) throws ResponseContextException {
        R entry = getDao().getByKey(key);
        if (entry != null) {
            getDao().refresh(entry);
        }
        return entry;

    }

    @Override
    public String[] getAccepts(RequestContext requestContext) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
    }

    @Override
    public R postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported.");
        return null;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        getDao().softDelete(key);
    }

    @Override
    public Iterable<R> getEntries(RequestContext requestContext) throws ResponseContextException {
        return getRecords(requestContext);
    }

    @Override
    public Object getContent(R entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public void putEntry(R entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

	@Override
	public String getAuthor(RequestContext requestContext) throws ResponseContextException {
	    return RegistryApplication.getApplicationContext().getUriPrefix();
	}

	@Override
	public String getId(RequestContext requestContext) {
	    return Constants.UQ_REGISTRY_URI_PREFIX + getBasePath();
	}

	@Override
    public String getTitle(RequestContext request) {
        return getTitle();
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        getHttpMethodHelper().addFeedDetails(feed, request, getRecordClass());
        Iterable<R> entries = getEntries(request);
        if (entries == null) {
        	return;
        }
        for (R entryObj : entries) {
            Entry e = feed.addEntry();
            IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
            addEntryDetails(request, e, feedIri, entryObj);
            getFeedOutputHelper().setPublished(entryObj, e);
            if (isMediaEntry(entryObj)) {
                addMediaContent(feedIri, e, entryObj, request);
            } else {
                addContent(e, entryObj, request);
                Link typeLink = e.addLink(getLinkTerm(), Constants.REL_TYPE);
                typeLink.setTitle(getTitle());
            }
        }
    }

}