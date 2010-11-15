package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.data.access.CollectionDao;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionAdapter extends AbstractEntityCollectionAdapter<net.metadata.dataspace.data.model.base.Collection> {

    private Logger logger = Logger.getLogger(getClass());
    private CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        return HttpMethodHelper.postEntry(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        return HttpMethodHelper.postMedia(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        return HttpMethodHelper.putEntry(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        return HttpMethodHelper.putMedia(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        return HttpMethodHelper.deleteEntry(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        return HttpMethodHelper.getEntry(request, net.metadata.dataspace.data.model.base.Collection.class);
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
            }
        } else {
            String accept = request.getAccept();
            if (accept.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "collection.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        net.metadata.dataspace.data.model.base.Collection latestCollection = collectionDao.getMostRecentUpdated();
        if (latestCollection != null) {
            collectionDao.refresh(latestCollection);
            feed.setUpdated(latestCollection.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.HTML_MIME_TYPE) || acceptHeader.equals(Constants.ATOM_FEED_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.HTML_MIME_TYPE;
            }
        }
        String atomFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            FeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            FeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }
        feed.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_COLLECTIONS);
        Iterable<net.metadata.dataspace.data.model.base.Collection> entries = getEntries(request);
        if (entries != null) {
            for (net.metadata.dataspace.data.model.base.Collection entryObj : entries) {
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

    public List<Person> getAuthors(net.metadata.dataspace.data.model.base.Collection collection, RequestContext request) throws ResponseContextException {
        Set<String> authors = collection.getAuthors();
        List<Person> personList = new ArrayList<Person>();
        for (String author : authors) {
            Person person = request.getAbdera().getFactory().newAuthor();
            person.setName(author);
            personList.add(person);
        }
        return personList;
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_ENTRY_MIMETYPE, Constants.JSON_MIMETYPE};
    }

    @Override
    public net.metadata.dataspace.data.model.base.Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                                                       Content content, RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        collectionDao.softDelete(key);
    }

    @Override
    public Object getContent(net.metadata.dataspace.data.model.base.Collection collection, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(collection.getContent());
        return content;
    }

    @Override
    public Iterable<net.metadata.dataspace.data.model.base.Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getAllActive();
    }

    @Override
    public net.metadata.dataspace.data.model.base.Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(key);
        if (collection != null) {
            collectionDao.refresh(collection);
        }
        return collection;
    }

    @Override
    public String getId(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    public String getName(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey();
    }

    @Override
    public String getTitle(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public Date getUpdated(net.metadata.dataspace.data.model.base.Collection collection) throws ResponseContextException {
        return collection.getUpdated();
    }

    @Override
    public void putEntry(net.metadata.dataspace.data.model.base.Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_COLLECTIONS;
    }

}
