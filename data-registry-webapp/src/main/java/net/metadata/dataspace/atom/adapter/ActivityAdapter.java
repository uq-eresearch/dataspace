package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.data.model.Activity;
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
 * Time: 3:24:08 PM
 */
public class ActivityAdapter extends AbstractEntityCollectionAdapter<Activity> {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public Activity postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
    }

    @Override
    public Object getContent(Activity entry, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Iterable<Activity> getEntries(RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Activity getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public String getId(Activity entry) throws ResponseContextException {
        return null;
    }

    @Override
    public String getName(Activity entry) throws ResponseContextException {
        return null;
    }

    @Override
    public String getTitle(Activity entry) throws ResponseContextException {
        return null;
    }

    @Override
    public Date getUpdated(Activity entry) throws ResponseContextException {
        return null;
    }

    @Override
    public void putEntry(Activity entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
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
