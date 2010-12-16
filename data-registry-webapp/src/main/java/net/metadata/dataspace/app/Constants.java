package net.metadata.dataspace.app;

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
     * Target Type names
     */
    String TARGET_TYPE_VERSION = "version";
    String TARGET_TYPE_WORKING_COPY = "working-copy";
    String TARGET_TYPE_VERSION_HISTORY = "version-history";
    String TARGET_LOGIN = "login";
    String TARGET_LOGOUT = "logout";

    /**
     * rel attribute types
     */
    String REL_ALTERNATE = "alternate";
    String REL_CREATOR = "http://purl.org/dc/terms/creator";
    String REL_DESCRIBES = "http://www.openarchives.org/ore/terms/describes";
    String REL_HAS_PARTICIPANT = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#hasParticipant";
    String REL_HAS_OUTPUT = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#hasOutput";
    String REL_IS_ACCESSED_VIA = "http://purl.org/cld/terms/isAccessedVia";
    String REL_IS_COLLECTOR_OF = "http://xmlns.com/foaf/0.1/made";
    String REL_IS_LOCATED_AT = "http://purl.org/cld/terms/isLocatedAt";
    String REL_IS_OUTPUT_OF = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf";
    String REL_IS_PARTICIPANT_IN = "http://xmlns.com/foaf/0.1/currentProject";
    String REL_IS_SUPPORTED_BY = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isSupportedBy";
    String REL_LATEST_VERSION = "latest-version";
    String REL_PREDECESSOR_VERSION = "predecessor-version";
    String REL_PUBLISHER = "http://purl.org/dc/terms/publisher";
    String REL_SELF = "self";
    String REL_SUCCESSOR_VERSION = "successor-version";
    String REL_VIA = "via";

    /**
     * scheme attributes
     */
    String SCHEME_DCMITYPE = "http://purl.org/dc/dcmitype/";
    String SCHEME_VIVO = "http://vivoweb.org/ontology/core#";
    String SCHEME_FOAF = "http://xmlns.com/foaf/0.1/";
    String SCHEME_ANDS_GROUP = "https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php";


    /**
     * term attributes
     */
    String TERM_ANDS_GROUP = "The University of Queensland";
    String TERM_ACTIVITY = SCHEME_FOAF + "Project";
    String TERM_COLLECTION = SCHEME_DCMITYPE + "Collection";
    String TERM_PARTY_AS_GROUP = SCHEME_FOAF + "Group";
    String TERM_PARTY_AS_AGENT = SCHEME_FOAF + "Agent";
    String TERM_SERVICE = SCHEME_VIVO + "Service";

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
}
