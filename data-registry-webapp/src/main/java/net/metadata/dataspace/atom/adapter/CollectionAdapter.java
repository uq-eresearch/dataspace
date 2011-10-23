package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.record.Collection;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
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
public class CollectionAdapter extends AbstractEntityCollectionAdapter<net.metadata.dataspace.data.model.record.Collection> {

    private Logger logger = Logger.getLogger(getClass());
    private CollectionDao collectionDao;

    public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public void setCollectionDao(CollectionDao collectionDao) {
		this.collectionDao = collectionDao;
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
        	return HttpMethodHelper.getInstance().postEntry(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResponseContext postMedia(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().postMedia(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResponseContext putEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().putEntry(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResponseContext putMedia(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().putMedia(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().deleteEntry(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly=true)
    public ResponseContext getEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getInstance().getEntry(request, net.metadata.dataspace.data.model.record.Collection.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly=true)
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
//                return HttpMethodHelper.getInstance().getFeed(request, Collection.class);
                Feed feed = createFeedBase(request);
                addFeedDetails(feed, request);
                ResponseContext responseContext = buildGetFeedResponse(feed);

                return HttpMethodHelper.getInstance().getFeed(request, responseContext, Collection.class);
            }
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        HttpMethodHelper.getInstance().addFeedDetails(feed, request, Collection.class);
        Iterable<Collection> entries = getEntries(request);
        if (entries != null) {
            for (Collection entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                FeedOutputHelper.setPublished(entryObj, e);
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

    @Transactional(readOnly=true)
    public List<Person> getAuthors(net.metadata.dataspace.data.model.record.Collection collection, RequestContext request) throws ResponseContextException {
        return HttpMethodHelper.getInstance().getAuthors(collection, request);
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
        collectionDao.softDelete(key);
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
        return HttpMethodHelper.getInstance().getRecords(requestContext, Collection.class);
    }

    @Override
    @Transactional(readOnly=true)
    public net.metadata.dataspace.data.model.record.Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        net.metadata.dataspace.data.model.record.Collection collection = collectionDao.getByKey(key);
        if (collection != null) {
            collectionDao.refresh(collection);
        }
        return collection;
    }

    @Override
    @Transactional(readOnly=true)
    public String getId(net.metadata.dataspace.data.model.record.Collection collection) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    @Transactional(readOnly=true)
    public String getName(net.metadata.dataspace.data.model.record.Collection collection) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    @Transactional(readOnly=true)
    public String getTitle(net.metadata.dataspace.data.model.record.Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    @Transactional(readOnly=true)
    public Date getUpdated(net.metadata.dataspace.data.model.record.Collection collection) throws ResponseContextException {
        return collection.getUpdated();
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
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_COLLECTIONS;
    }

}
