package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.apache.abdera.protocol.server.ProviderHelper.calculateEntityTag;

/**
 * User: alabri
 * Date: 20/10/2010
 * Time: 2:13:08 PM
 */
@Transactional
public class FeedOutputHelper {

    public static String getRepresentationMimeType(RequestContext request) {
        String fullUrl = request.getUri().toString();
        String representation = null;
        if (fullUrl.contains("?repr")) {
            representation = fullUrl.split("repr=")[1];
        }
        return representation;
    }

    public static ResponseContext getHtmlRepresentationOfFeed(RequestContext request, ResponseContext responseContext, Class<?> clazz) {
        responseContext.setContentType(Constants.MIME_TYPE_HTML);
        String xslFilePath = "/files/xslt/feed/xhtml/atom2xhtml-feed.xsl";
        String viewRepresentation = OperationHelper.getViewRepresentation(request);
        if (viewRepresentation != null && viewRepresentation.equals("new")) {
            xslFilePath = "/files/xslt/xhtml/add/new-atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
        }
        XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath, request);
        responseContext.setWriter(writer);
        return responseContext;
    }

    public static void prepareFeedSelfLink(Feed feed, String selfLinkHref, String mimeType) {
        feed.getSelfLink().setHref(selfLinkHref);
        feed.getSelfLink().setRel(Constants.REL_SELF);
        feed.getSelfLink().setMimeType(mimeType);
    }


    public static void prepareFeedAlternateLink(Feed feed, String alternateLinkHref, String mimeType) {
        feed.getAlternateLink().setHref(alternateLinkHref);
        feed.getAlternateLink().setRel(Constants.REL_ALTERNATE);
        feed.getAlternateLink().setMimeType(mimeType);
    }

    public static ResponseContext getVersionHistoryFeed(RequestContext request, Feed feed, Record record, Class<?> clazz) throws ResponseContextException {
        SortedSet<Version> versions = record.getVersions();
        for (Version version : versions) {
            Entry entry = feed.addEntry();
            String uri = feed.getId() + "/" + version.getUriKey();
            entry.setId(uri);
            entry.setTitle(version.getTitle());
            entry.setContent(version.getDescription());
            Link selfLink = entry.addLink(uri, Constants.REL_SELF);
            selfLink.setMimeType(Constants.MIME_TYPE_ATOM_ENTRY);
            List<Person> personList = HttpMethodHelper.getInstance().getAuthors(record, request);
            
            entry.setUpdated(version.getUpdated());
            setPublished(version, entry);
        }
        feed.setUpdated(new Date());
        Link link = feed.addLink(feed.getId() + "/" + Constants.TARGET_TYPE_VERSION_HISTORY, Constants.TARGET_TYPE_VERSION_HISTORY);
        link.setMimeType(Constants.MIME_TYPE_ATOM_FEED);
        Document<Feed> document = feed.getDocument();
        AbstractResponseContext responseContext = new BaseResponseContext<Document<Feed>>(document);
        responseContext.setEntityTag(calculateEntityTag(document.getRoot()));

        String accept = OperationHelper.getAcceptHeader(request);
        responseContext.setLocation(feed.getId().toString());
        responseContext.setHeader("Vary", "Accept");
        if (accept.equals(Constants.MIME_TYPE_XHTML)) {
            ResponseContext htmlRepresentationOfFeed = getHtmlRepresentationOfFeed(request, responseContext, clazz);
//                if (request.getHeader("user-agent").indexOf("MSIE ") > -1) {
            responseContext.setContentType(Constants.MIME_TYPE_HTML);
//                } else {
//                    responseContext.setContentType(Constants.MIME_TYPE_XHTML);
//                }
            return htmlRepresentationOfFeed;
        } else {
            return responseContext;
        }
    }

    public static Feed createVersionFeed(RequestContext request) {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        String uri = request.getUri().toString();
        feed.setId(Constants.UQ_REGISTRY_URI_PREFIX + uri.substring(1));
        feed.setTitle("Version History");
        feed.addLink(uri, "self");
        return feed;
    }

    public static void setPublished(Version version, Entry entry) {
        Control control = entry.addControl();
        Version published = version.getParent().getPublished();
        //False is used her to indicate the version is published and true (isDraft) is not published
        if (published != null && version.equals(published)) {
            control.setDraft(false);
        } else {
            control.setDraft(true);
        }
    }

    public static void setPublished(Record record, Entry entry) {
        Control control = entry.addControl();
        Version published = record.getPublished();
        //False is used her to indicate the version is published and true (isDraft) is not published
        if (published != null) {
            control.setDraft(false);
        } else {
            control.setDraft(true);
        }
    }

}
