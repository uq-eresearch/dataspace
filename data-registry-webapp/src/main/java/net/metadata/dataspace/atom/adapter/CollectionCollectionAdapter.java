package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.model.Collection;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionCollectionAdapter extends AbstractEntityCollectionAdapter<Collection> {

    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getCollectionDao();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix() + "collection/";

    @Override
    public Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                Content content, RequestContext requestContext) throws ResponseContextException {
        Collection collection = new Collection();
        collection.setTitle(title);
        collection.setSummary(summary);
        collection.setUpdated(updated);
        collection.setAuthors(getAuthors(authors));
        collectionDao.save(collection);
        return collection;
    }

    @Override
    public void putEntry(Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {
        collectionDao.update(collection);
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        collectionDao.delete(collectionDao.getByKey(key));
    }

    @Override
    public Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getContent(Collection collection, RequestContext requestContext) throws ResponseContextException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getAll();
    }

    @Override
    public String getId(Collection collection) throws ResponseContextException {
        return collection.getKey();
    }

    @Override
    public String getName(Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public String getTitle(Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public Date getUpdated(Collection collection) throws ResponseContextException {
        return collection.getUpdated();
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return ID_PREFIX + "collection/collections";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Collections";
    }

    private List<String> getAuthors(List<Person> persons) {
        List<String> authors = new ArrayList<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }

}
