package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Service;
import net.metadata.dataspace.util.AtomFeedHelper;
import net.metadata.dataspace.util.CollectionAdapterHelper;
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

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:22:53 PM
 */
public class ServiceAdapter extends AbstractEntityCollectionAdapter<Service> {

    private Logger logger = Logger.getLogger(getClass());
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();
    private ServiceDao serviceDao = DataRegistryApplication.getApplicationContext().getDaoManager().getServiceDao();

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Service service = serviceDao.getByKey(uriKey);
        if (service == null) {
            return ProviderHelper.notfound(request);
        } else {
            serviceDao.refresh(service);
            if (service.isActive()) {
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
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Service service = serviceDao.getByKey(uriKey);
        if (service == null) {
            return ProviderHelper.notfound(request);
        } else {
            serviceDao.refresh(service);
            if (service.isActive()) {
                Entry entry = CollectionAdapterHelper.getEntryFromService(service);
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
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "service.jsp");
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
                return AtomFeedHelper.getHtmlRepresentationOfFeed(request, "service.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        Service latestService = serviceDao.getMostRecentUpdated();
        if (latestService != null) {
            serviceDao.refresh(latestService);
            feed.setUpdated(latestService.getUpdated());
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
        String atomFeedUrl = ID_PREFIX + Constants.SERVICES_PATH + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = ID_PREFIX + Constants.SERVICES_PATH;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            AtomFeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            AtomFeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            AtomFeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            AtomFeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }

        feed.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.SERVICES_TITLE);
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
        return serviceDao.getAllActive();
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
        return ID_PREFIX + Constants.SERVICES_PATH + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Service entry) throws ResponseContextException {
        return ID_PREFIX + Constants.SERVICES_PATH + "/" + entry.getUriKey();
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
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext request) {
        return ID_PREFIX + Constants.SERVICES_PATH;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.SERVICES_TITLE;
    }
}
