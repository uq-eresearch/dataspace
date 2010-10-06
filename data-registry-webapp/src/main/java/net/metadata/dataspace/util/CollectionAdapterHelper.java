package net.metadata.dataspace.util;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
public class CollectionAdapterHelper {
    public static String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY)
            return null;
        String[] segments = request.getUri().toString().split("/");
        return UrlEncoding.decode(segments[segments.length - 1]);
    }
}
