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
    String MIME_TYPE_ATOM = "application/atom+xml";
    String MIME_TYPE_ATOM_ENTRY = "application/atom+xml;type=entry";
    String MIME_TYPE_ATOM_FEED = "application/atom+xml;type=feed";
    String MIME_TYPE_HTML = "text/html";
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

    /**
     * Titles
     */
    String TITLE_FOR_AGENTS = "Agents";
    String TITLE_FOR_COLLECTIONS = "Collections";
    String TITLE_FOR_ACTIVITIES = "Activities";
    String TITLE_FOR_SERVICES = "Services";

    String UQ_REGISTRY_URI_PREFIX = RegistryApplication.getApplicationContext().getUriPrefix();
    String UQ_REGISTRY_TITLE = RegistryApplication.getApplicationContext().getRegistryTitle();
    String UQ_REGISTRY_EMAIL = RegistryApplication.getApplicationContext().getRegistryEmail();
    String UQ_REGISTRY_LICENSE = RegistryApplication.getApplicationContext().getRegistryLicense();
    String UQ_REGISTRY_RIGHTS = RegistryApplication.getApplicationContext().getRegistryRights();
    /**
     * Target Type names
     */
    String TARGET_TYPE_VERSION = "version";
    String TARGET_TYPE_WORKING_COPY = "working-copy";
    String TARGET_TYPE_VERSION_HISTORY = "version-history";
    String TARGET_LOGIN = "login";
    String TARGET_LOGOUT = "logout";

    /**
     * Namespaces
     */
    String NS_ANDS_GROUP = "https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php";
    String NS_FOAF = "http://xmlns.com/foaf/0.1/";
    String NS_ANDS = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#";
    String NS_DC = "http://purl.org/dc/terms/";
    String NS_DCMITYPE = "http://purl.org/dc/dcmitype/";
    String NS_CLD = "http://purl.org/cld/terms/";
    String NS_VIVO = "http://vivoweb.org/ontology/core#";
    String NS_ORE = "http://www.openarchives.org/ore/terms/";
    String NS_GEORSS = "http://www.georss.org/georss/";
    String NS_RDFA = "http://www.w3.org/ns/rdfa#";

    /**
     * rel attribute types
     */
    String REL_ACCESS_RIGHTS = NS_DC + "accessRights";
    String REL_ALTERNATE = "alternate";
    String REL_CREATOR = NS_DC + "creator";
    String REL_DESCRIBES = NS_ORE + "describes";
    String REL_HAS_PARTICIPANT = NS_ANDS + "hasParticipant";
    String REL_HAS_OUTPUT = NS_ANDS + "hasOutput";
    String REL_IS_ACCESSED_VIA = NS_CLD + "isAccessedVia";
    String REL_IS_COLLECTOR_OF = NS_FOAF + "made";
    String REL_IS_DESCRIBED_BY = NS_ORE + "isDescribedBy";
    String REL_IS_LOCATED_AT = NS_CLD + "isLocatedAt";
    String REL_IS_OUTPUT_OF = NS_ANDS + "isOutputOf";
    String REL_CURRENT_PROJECT = NS_FOAF + "currentProject";
    String REL_IS_SUPPORTED_BY = NS_ANDS + "isSupportedBy";
    String REL_LATEST_VERSION = "latest-version";
    String REL_PAGE = NS_FOAF + "page";
    String REL_PREDECESSOR_VERSION = "predecessor-version";
    String REL_PUBLISHER = NS_DC + "publisher";
    String REL_RELATED = "related";
    String REL_SELF = "self";
    String REL_SUCCESSOR_VERSION = "successor-version";
    String REL_TEMPORAL = NS_DC + "temporal";
    String REL_SPATIAL = NS_DC + "spatial";
    String REL_VIA = "via";


    /**
     * term attributes
     */
    String TERM_ANDS_GROUP = "The University of Queensland";
    String TERM_ACTIVITY = NS_FOAF + "Project";
    String TERM_COLLECTION = NS_DCMITYPE + "Collection";
    String TERM_AGENT_AS_GROUP = NS_FOAF + "Group";
    String TERM_AGENT_AS_AGENT = NS_FOAF + "Agent";
    String TERM_SERVICE = NS_VIVO + "Service";

    /**
     * HTTP Status messages
     */
    String HTTP_STATUS_200 = "OK";
    String HTTP_STATUS_400 = "Bad Request";
    String HTTP_STATUS_404 = "Entry not found";
    String HTTP_STATUS_410 = "Gone";
    String HTTP_STATUS_415 = "Unsupported Media Type";
    String HTTP_STATUS_401 = "Unauthorized";

    /**
     *
     */
    String SESSION_ATTRIBUTE_CURRENT_USER = "currentUser";
    String SESSION_ATTRIBUTE_LDAP_CONTEXT = "currentUser";

    /**
     * QNames
     */
    QName QNAME_RDFA_META = new QName(Constants.NS_RDFA, "meta", "rdfa");
    QName QNAME_GEO_RSS_POINT = new QName(Constants.NS_GEORSS, "point", "georss");
    QName QNAME_GEO_RSS_BOX = new QName(Constants.NS_GEORSS, "box", "georss");
    QName QNAME_GEO_RSS_FEATURE_NAME = new QName(Constants.NS_GEORSS, "featureName", "georss");

}
