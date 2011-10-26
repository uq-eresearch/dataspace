package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.model.record.Collection;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionAdapter extends AbstractRecordAdapter<Collection> {

	@Override
	protected Class<Collection> getRecordClass() {
		return Collection.class;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResponseContext postEntry(RequestContext request) {
        try {
        	EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getEntityManagerSource().getEntityManager();
	        try {
	        	entityManager.flush();
	        } catch (PersistenceException e) {
	        	throw e;
	        }
        	return getHttpMethodHelper().postEntry(request, getRecordClass());
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        getHttpMethodHelper().addFeedDetails(feed, request, Collection.class);
        Iterable<Collection> entries = getEntries(request);
        if (entries != null) {
            for (Collection entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                getFeedOutputHelper().setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                    Link typeLink = e.addLink(Constants.TERM_COLLECTION, Constants.REL_TYPE);
                    typeLink.setTitle("Collection");
                }
            }
        }
    }

    @Override
    @Transactional(readOnly=true)
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
    }

    @Override
    public net.metadata.dataspace.data.model.record.Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                                                         Content content, RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        getDao().softDelete(key);
    }

    @Override
    @Transactional(readOnly=true)
    public Object getContent(net.metadata.dataspace.data.model.record.Collection collection, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(collection.getContent());
        return content;
    }

    @Override
    @Transactional(readOnly=true)
    public Iterable<net.metadata.dataspace.data.model.record.Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return getRecords(requestContext);
    }

    @Override
    @Transactional(readOnly=true)
    public net.metadata.dataspace.data.model.record.Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        net.metadata.dataspace.data.model.record.Collection collection = getDao().getByKey(key);
        if (collection != null) {
            getDao().refresh(collection);
        }
        return collection;
    }

    @Override
    public void putEntry(net.metadata.dataspace.data.model.record.Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {
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
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_COLLECTIONS;
    }

	@Override
	protected String getBasePath() {
		return Constants.PATH_FOR_COLLECTIONS;
	}

}
