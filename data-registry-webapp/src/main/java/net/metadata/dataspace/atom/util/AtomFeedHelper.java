package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.MediaResponseContext;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: alabri
 * Date: 20/10/2010
 * Time: 2:13:08 PM
 */
public class AtomFeedHelper {

    private static final String URL = DataRegistryApplication.getApplicationContext().getUriPrefix();

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
            URL url = new URL(URL + template);
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
        feed.getSelfLink().setRel("self");
        feed.getSelfLink().setMimeType(mimeType);
    }


    public static void prepareFeedAlternateLink(Feed feed, String alternateLinkHref, String mimeType) {
        feed.getAlternateLink().setHref(alternateLinkHref);
        feed.getAlternateLink().setRel("alternate");
        feed.getAlternateLink().setMimeType(mimeType);
    }
}
