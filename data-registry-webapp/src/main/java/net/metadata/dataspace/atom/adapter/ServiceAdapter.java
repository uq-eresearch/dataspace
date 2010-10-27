package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Service;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
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
