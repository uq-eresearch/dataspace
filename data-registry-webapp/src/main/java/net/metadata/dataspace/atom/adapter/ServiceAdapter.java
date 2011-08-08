package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.record.Service;
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
 * Time: 3:22:53 PM
 */
@Transactional
public class ServiceAdapter extends AbstractEntityCollectionAdapter<Service> {

    private Logger logger = Logger.getLogger(getClass());
    private ServiceDao serviceDao;
    
    public ServiceDao getServiceDao() {
		return serviceDao;
	}

	public void setServiceDao(ServiceDao serviceDao) {
		this.serviceDao = serviceDao;
	}

	@Override
    public ResponseContext postEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().postEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().postMedia(request, Service.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().putEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().putMedia(request, Service.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().deleteEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().getEntry(request, Service.class);
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
                return HttpMethodHelper.getInstance().getFeed(request, responseContext, Service.class);
            }
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        HttpMethodHelper.getInstance().addFeedDetails(feed, request, Service.class);
        Iterable<Service> entries = getEntries(request);
        if (entries != null) {
            for (Service entryObj : entries) {
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

    public List<Person> getAuthors(Service service, RequestContext request) throws ResponseContextException {
        return HttpMethodHelper.getInstance().getAuthors(service, request);
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
    }

    @Override
    public Service postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext request) throws ResponseContextException {
        serviceDao.softDelete(key);
    }

    @Override
    public Object getContent(Service entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public Iterable<Service> getEntries(RequestContext requestContext) throws ResponseContextException {
        return HttpMethodHelper.getInstance().getRecords(requestContext, Service.class);
    }

    @Override
    public Service getEntry(String key, RequestContext request) throws ResponseContextException {
        Service service = serviceDao.getByKey(key);
        if (service != null) {
            serviceDao.refresh(service);
        }
        return service;

    }

    @Override
    public String getId(Service entry) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Service entry) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
    }

    @Override
    public String getTitle(Service entry) throws ResponseContextException {
        return entry.getTitle();
    }

    @Override
    public Date getUpdated(Service entry) throws ResponseContextException {
        return entry.getUpdated();
    }

    @Override
    public void putEntry(Service entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext request) {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_SERVICES;
    }

}
