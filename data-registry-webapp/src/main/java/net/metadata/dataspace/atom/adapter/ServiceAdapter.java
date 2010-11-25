package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.base.Service;
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
 * Time: 3:22:53 PM
 */
public class ServiceAdapter extends AbstractEntityCollectionAdapter<Service> {

    private Logger logger = Logger.getLogger(getClass());
    private ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        try {
            return HttpMethodHelper.postEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        try {
            return HttpMethodHelper.postMedia(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        try {
            return HttpMethodHelper.putEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        try {
            return HttpMethodHelper.putMedia(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            return HttpMethodHelper.deleteEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getEntry(request, Service.class);
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        try {
            String representationMimeType = FeedHelper.getRepresentationMimeType(request);
            String accept = request.getAccept();
            if ((representationMimeType != null && representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) || (accept != null && accept.equals(Constants.ATOM_FEED_MIMETYPE))) {
                return super.getFeed(request);
            } else {
                return HttpMethodHelper.getFeed(request, Service.class);
            }
        } catch (ResponseContextException e) {
            return ProviderHelper.createErrorResponse(request.getAbdera(), e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        HttpMethodHelper.addFeedDetails(feed, request, Service.class);
        Iterable<Service> entries = getEntries(request);
        if (entries != null) {
            for (Service entryObj : entries) {
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
    public Iterable<Service> getEntries(RequestContext request) throws ResponseContextException {
        return serviceDao.getAllPublished();
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
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Service entry) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
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
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_SERVICES;
    }

}
