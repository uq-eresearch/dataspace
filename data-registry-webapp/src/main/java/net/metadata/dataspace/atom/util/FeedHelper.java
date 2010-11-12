package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.MediaResponseContext;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import static org.apache.abdera.protocol.server.ProviderHelper.calculateEntityTag;

/**
 * User: alabri
 * Date: 20/10/2010
 * Time: 2:13:08 PM
 */
public class FeedHelper {

    public static String getRepresentationMimeType(RequestContext request) {
        String fullUrl = request.getUri().toString();
        String representation = null;
        if (fullUrl.contains("?repr")) {
            representation = fullUrl.split("repr=")[1];
        }
        return representation;
    }

    public static ResponseContext getHtmlRepresentationOfFeed(RequestContext request, String template) {
        try {
            URL url = new URL(Constants.ID_PREFIX + template);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            MediaResponseContext rc = new MediaResponseContext(conn.getInputStream(), 200);
            rc.setContentType(Constants.HTML_MIME_TYPE);
            return rc;
        } catch (IOException e) {
            return ProviderHelper.servererror(request, e);
        }
    }


    public static void prepareFeedSelfLink(Feed feed, String selfLinkHref, String mimeType) {
        feed.getSelfLink().setHref(selfLinkHref);
        feed.getSelfLink().setRel(Constants.REL_TYPE_SELF);
        feed.getSelfLink().setMimeType(mimeType);
    }


    public static void prepareFeedAlternateLink(Feed feed, String alternateLinkHref, String mimeType) {
        feed.getAlternateLink().setHref(alternateLinkHref);
        feed.getAlternateLink().setRel(Constants.REL_TYPE_ALTERNATE);
        feed.getAlternateLink().setMimeType(mimeType);
    }

    public static ResponseContext getVersionHistoryFeed(Feed feed, Record record) {
        SortedSet<Version> versions = record.getVersions();
        for (Version version : versions) {
            Entry entry = feed.addEntry();
            entry.setId(feed.getId() + "/" + version.getUriKey());
            entry.setTitle(version.getTitle());
            entry.setSummary(version.getSummary());
            Set<String> authors = version.getAuthors();
            for (String author : authors) {
                entry.addAuthor(author);
            }
            entry.setUpdated(version.getUpdated());
        }
        feed.setUpdated(new Date());
        Document<Feed> document = feed.getDocument();
        AbstractResponseContext responseContext = new BaseResponseContext<Document<Feed>>(document);
        responseContext.setEntityTag(calculateEntityTag(document.getRoot()));
        return responseContext;
    }

    public static Feed createVersionFeed(RequestContext request, String id) {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        String uri = id + "/" + Constants.TARGET_TYPE_VERSION_HISTORY;
        feed.setId(uri);
        feed.setTitle("Version History");
        feed.addLink(uri, "self");
        return feed;
    }
}
