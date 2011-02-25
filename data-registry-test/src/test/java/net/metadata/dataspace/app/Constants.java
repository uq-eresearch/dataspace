package net.metadata.dataspace.app;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 3:14:35 PM
 */
public interface Constants {

    /**
     * Mime types
     */
    String JSON_MIMETYPE = "application/json";
    String ATOM_MIMETYPE = "application/atom+xml";
    String ATOM_ENTRY_MIMETYPE = "application/atom+xml;type=entry";
    String ATOM_FEED_MIMETYPE = "application/atom+xml;type=feed";
    String HTML_MIME_TYPE = "text/html";
    String ATOM_SERVICE_MIMETYPE = "application/atomsvc+xml";
    String MIME_TYPE_RDF = "application/rdf+xml";
    String MIME_TYPE_RIFCS = "application/rifcs+xml";
    String MIME_TYPE_XHTML = "application/xhtml+xml";

    /**
     * Adapters
     */
    String PATH_FOR_AGENTS = "agents";
    String PATH_FOR_COLLECTIONS = "collections";
    String PATH_FOR_ACTIVITIES = "activities";
    String PATH_FOR_SERVICES = "services";
    String PATH_FOR_ATOM_SERVICE = "registry.atomsvc";
    /**
     * Titles
     */
    String TITLE_FOR_AGENTS = "Agents";
    String TITLE_FOR_COLLECTIONS = "Collections";
    String TITLE_FOR_ACTIVITIES = "Activities";
    String TITLE_FOR_SERVICES = "Services";

    String URL_PREFIX = "http://localhost:9635/";
//    String URL_PREFIX = "http://localhost:8080/";

    /**
     * Target Type names
     */
    String TARGET_TYPE_VERSION = "version";

    /**
     * rel attribute types
     */
    String REL_TYPE_SELF = "self";
    String REL_TYPE_ALTERNATE = "alternate";
    String REL_TYPE_LATEST_VERSION = "latest-version";
    String REL_TYPE_PREDECESSOR_VERSION = "predecessor-version";
    String REL_TYPE_SUCCESSOR_VERSION = "successor-version";

    /**
     * HTTP Status messages
     */
    String HTTP_STATUS_200 = "OK";
    String HTTP_STATUS_400 = "Bad Request";
    String HTTP_STATUS_410 = "Gone";
    String HTTP_STATUS_415 = "Unsupported Media Type";

    /**
     * Credentials
     */
    String USERNAME = "test";
    String PASSWORD = "test";

    String TEST_CONTEXT = "/conf/spring/testContext.xml";

    String FEED_PATH = "/atom:feed";
    String FEED_ID_PATH = "/atom:feed/atom:id";
    String FEED_TITLE_PATH = "/atom:feed/atom:title";
    String FEED_UPDATED_PATH = "/atom:feed/atom:updated";
    String FEED_AUTHOR_NAME_PATH = "/atom:feed/atom:author/atom:name";
    String FEED_LINK_PATH = "/atom:feed/atom:link";

    String RECORD_ID_PATH = "/atom:entry/atom:id";
    String RECORD_TITLE_PATH = "/atom:entry/atom:title";
    String RECORD_CONTENT_PATH = "/atom:entry/atom:content";
    String RECORD_UPDATED_PATH = "/atom:entry/atom:updated";
    String RECORD_DRAFT_PATH = "/atom:entry/app:control/app:draft";
    String RECORD_AUTHOR_NAME_PATH = "/atom:entry/atom:author/atom:name";
    String RECORD_LINK_PATH = "/atom:entry/atom:link";

}
