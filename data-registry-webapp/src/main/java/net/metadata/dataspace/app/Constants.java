package net.metadata.dataspace.app;

import javax.xml.namespace.QName;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 8:16:32 AM
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


    /**
     * Namespace related
     */
    String NAMESPACE = "http://dataspace.metadata.net/";
    String PREFIX = "uqdata";


    /**
     * Element names
     */
    String ELEMENT_NAME_TITLE = "title";
    String ELEMENT_NAME_SUMMARY = "summary";
    String ELEMENT_NAME_CONTENT = "content";
    String ELEMENT_NAME_UPDATED = "updated";
    String ELEMENT_NAME_AUTHOR = "author";
    String ELEMENT_NAME_AUTHORS = "authors";
    String ELEMENT_NAME_SUBJECT = "subject";
    String ELEMENT_NAME_COLLECTOR_OF = "collectorOf";
    String ELEMENT_NAME_SUPPORTED_BY = "supportedBy";
    String ELEMENT_NAME_HAS_OUTPUT = "hasOutput";
    String ELEMENT_NAME_HAS_PARTICIPANT = "hasParticipant";
    String ELEMENT_NAME_IS_PARTICIPANT_IN = "isParticipantIn";
    String ELEMENT_NAME_COLLECTOR = "collector";
    String ELEMENT_NAME_LOCATION = "location";
    String ELEMENT_NAME_SUPPORTS = "supports";
    String ELEMENT_NAME_IS_OUTPUT_OF = "isOutputOf";

    /**
     * Attribute names
     */
    String ATTRIBUTE_NAME_URI = "uri";
    String ATTRIBUTE_NAME_VOCABULARY = "vocabulary";
    String ATTRIBUTE_NAME_VALUE = "value";

    /**
     * Adapters
     */
    String PATH_FOR_PARTIES = "parties";
    String PATH_FOR_COLLECTIONS = "collections";
    String PATH_FOR_ACTIVITIES = "activities";
    String PATH_FOR_SERVICES = "services";

    /**
     * Titles
     */
    String TITLE_FOR_PARTIES = "Parties";
    String TITLE_FOR_COLLECTIONS = "Collections";
    String TITLE_FOR_ACTIVITIES = "Activities";
    String TITLE_FOR_SERVICES = "Services";

    String ID_PREFIX = RegistryApplication.getApplicationContext().getUriPrefix();

    /**
     * Qualified names
     */
    QName QNAME_SUBJECT = new QName(NAMESPACE, ELEMENT_NAME_SUBJECT, PREFIX);
    QName QNAME_COLLECTOR_OF = new QName(NAMESPACE, ELEMENT_NAME_COLLECTOR_OF, PREFIX);
    QName QNAME_COLLECTOR = new QName(NAMESPACE, ELEMENT_NAME_COLLECTOR, PREFIX);
    QName QNAME_HAS_OUTPUT = new QName(NAMESPACE, ELEMENT_NAME_HAS_OUTPUT, PREFIX);
    QName QNAME_IS_OUTPUT_OF = new QName(NAMESPACE, ELEMENT_NAME_IS_OUTPUT_OF, PREFIX);
    QName QNAME_HAS_PARTICIPANT = new QName(NAMESPACE, ELEMENT_NAME_HAS_PARTICIPANT, PREFIX);
    QName QNAME_IS_PARTICIPANT_IN = new QName(NAMESPACE, ELEMENT_NAME_IS_PARTICIPANT_IN, PREFIX);
    QName QNAME_LOCATION = new QName(NAMESPACE, ELEMENT_NAME_LOCATION, PREFIX);
    QName QNAME_SUPPORTED_BY = new QName(NAMESPACE, ELEMENT_NAME_SUPPORTED_BY, PREFIX);
    QName QNAME_SUPPORTS = new QName(NAMESPACE, ELEMENT_NAME_SUPPORTS, PREFIX);

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
    String HTTP_STATUS_401 = "Unauthorized";

    /**
     *
     */
    String SESSION_ATTRIBUTE_CURRENT_USER = "currentUser";

    String SESSION_ATTRIBUTE_LDAP_CONTEXT = "currentUser";
}
