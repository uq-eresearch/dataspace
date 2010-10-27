package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.data.model.Service;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:22:53 PM
 */
public class ServiceAdapter extends AbstractEntityCollectionAdapter<Service> {


    @Override
    public Service postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
    }

    @Override
    public Object getContent(Service entry, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Iterable<Service> getEntries(RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Service getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public String getId(Service entry) throws ResponseContextException {
        return null;
    }

    @Override
    public String getName(Service entry) throws ResponseContextException {
        return null;
    }

    @Override
    public String getTitle(Service entry) throws ResponseContextException {
        return null;
    }

    @Override
    public Date getUpdated(Service entry) throws ResponseContextException {
        return null;
    }

    @Override
    public void putEntry(Service entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public String getId(RequestContext request) {
        return null;
    }

    @Override
    public String getTitle(RequestContext request) {
        return null;
    }
}
