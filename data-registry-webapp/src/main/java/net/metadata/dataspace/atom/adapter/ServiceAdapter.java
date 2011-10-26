package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.record.Service;
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
 * Time: 3:22:53 PM
 */
@Transactional
public class ServiceAdapter extends AbstractRecordAdapter<Service> {

	@Override
	protected Class<Service> getRecordClass() {
		return Service.class;
	}

	@Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        getHttpMethodHelper().addFeedDetails(feed, request, getRecordClass());
        Iterable<Service> entries = getEntries(request);
        if (entries != null) {
            for (Service entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                getFeedOutputHelper().setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                    Link typeLink = e.addLink(Constants.TERM_SERVICE, Constants.REL_TYPE);
                    typeLink.setTitle("Service");
                }
            }
        }
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
        getDao().softDelete(key);
    }

    @Override
    public Object getContent(Service entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public Iterable<Service> getEntries(RequestContext requestContext) throws ResponseContextException {
        return getRecords(requestContext);
    }

    @Override
    public Service getEntry(String key, RequestContext request) throws ResponseContextException {
        Service service = getDao().getByKey(key);
        if (service != null) {
            getDao().refresh(service);
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
