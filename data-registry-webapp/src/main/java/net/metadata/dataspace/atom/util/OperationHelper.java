package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.context.SimpleResponseContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;

/**
 * Author: alabri
 * Date: 8/04/11
 * Time: 4:09 PM
 */
@Transactional
public class OperationHelper {

	private static String stripQueryAndFragments(String fullUrl) {
        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }
        if (fullUrl.contains("#")) {
            fullUrl = fullUrl.split("#")[0];
        }
		return fullUrl;
	}

    public static String getEntityID(String fullUrl) {
        fullUrl = stripQueryAndFragments(fullUrl);
        String[] segments = fullUrl.split("/");
        return UrlEncoding.decode(segments[segments.length - 1]);
    }


    public static String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY && request.getTarget().getType() != TargetType.get(Constants.TARGET_TYPE_VERSION)) {
            return null;
        }
        String fullUrl = request.getUri().toString();
        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }
        String[] segments = fullUrl.split("/");
        int segmentPos = segments.length - 1;
        if (request.getTarget().getType() == TargetType.get(Constants.TARGET_TYPE_VERSION)) {
            return UrlEncoding.decode(segments[segmentPos - 1]);
        }
        return UrlEncoding.decode(segments[segmentPos]);
    }

    public static String getEntryVersionID(RequestContext request) throws ResponseContextException {
        try {
            if (request.getTarget().getType() != TargetType.get(Constants.TARGET_TYPE_VERSION)) {
                return null;
            }
            String fullUrl = request.getUri().toString();
            if (fullUrl.contains("?")) {
                fullUrl = fullUrl.split("\\?")[0];
            }
            String[] segments = fullUrl.split("/");
            int segmentPos = segments.length - 1;
            return UrlEncoding.decode(segments[segmentPos]);
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    public static String getAcceptHeader(RequestContext request) {
        String representationMimeType = OperationHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader != null) {
                if (acceptHeader.contains(Constants.MIME_TYPE_ATOM_ENTRY) || acceptHeader.contains(Constants.MIME_TYPE_ATOM)) {
                    representationMimeType = Constants.MIME_TYPE_ATOM;
                } else if (acceptHeader.contains(Constants.MIME_TYPE_RDF)) {
                    representationMimeType = Constants.MIME_TYPE_RDF;
                } else if (acceptHeader.contains(Constants.MIME_TYPE_RIFCS)) {
                    representationMimeType = Constants.MIME_TYPE_RIFCS;
                } else {
//                    representationMimeType = Constants.MIME_TYPE_XHTML;
                    representationMimeType = Constants.MIME_TYPE_HTML;
                }
            } else {
//                representationMimeType = Constants.MIME_TYPE_XHTML;
                representationMimeType = Constants.MIME_TYPE_HTML;
            }
        }
        return representationMimeType;
    }

    public static String getRepresentationMimeType(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY && request.getTarget().getType() != TargetType.get(Constants.TARGET_TYPE_VERSION, true)) {
            return null;
        }
        String fullUrl = request.getUri().toString();
        String representation = null;
        if (fullUrl.contains("?repr")) {
            representation = fullUrl.split("repr=")[1];
        }
        return representation;
    }

    public static String getViewRepresentation(RequestContext request) {
        String parameter = request.getParameter("v");
        return parameter;
    }

    /**
     * Returns an Error document based on the StreamWriter
     */
    public static AbstractResponseContext createResponse(final int code,
                                                         final String message) {
        AbstractResponseContext rc = new SimpleResponseContext() {
            @Override
            protected void writeEntity(Writer writer) throws IOException {
                writeTo(writer);
            }

            @Override
            public boolean hasEntity() {
                return true;
            }

            @Override
            public void writeTo(Writer writer) throws IOException {
                writer.write(message);
            }
        };
        rc.setStatus(code);
        rc.setStatusText(message);
        return rc;
    }

}
