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
     * Mime types names
     */
    String MIM_TYPE_NAME_ATOM = "atom";
    String MIM_TYPE_NAME_RDF = "rdf";
    String MIM_TYPE_NAME_RIFCS = "rifcs";
    String MIM_TYPE_NAME_XHTML = "xhtml";
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

    String UQ_URL = "http://www.uq.edu.au";
    /**
     * Target Type names
     */
    String TARGET_TYPE_VERSION = "version";
    String TARGET_TYPE_VERSION_HISTORY = "version-history";
    String TARGET_LOGIN = "login";
    String TARGET_LOGOUT = "logout";

    String PERSISTENT_URL = "http://purl.org/";
    /**
     * Namespaces
     */
    String NS_FOAF = "http://xmlns.com/foaf/0.1/";
    String NS_ANDS = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#";
    String NS_DC = PERSISTENT_URL + "dc/terms/";
    String NS_DCMITYPE = PERSISTENT_URL + "dc/dcmitype/";
    String NS_CLD = PERSISTENT_URL + "cld/terms/";
    String NS_ANZSRC = PERSISTENT_URL + "asc/1297.0/2008/";
    String NS_VIVO = "http://vivoweb.org/ontology/core#";
    String NS_ORE = "http://www.openarchives.org/ore/terms/";
    String NS_GEORSS = "http://www.georss.org/georss";
    String NS_RDFA = "http://www.w3.org/ns/rdfa#";
    String NS_EFS = "http://www.e-framework.org/Contributions/ServiceGenres/";
    String NS_RDF_99 = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
    String NS_DATASPACE = "http://dataspace.uq.edu.au/vocab/";
    /**
     * rel attribute types
     */
    String REL_ACCESS_RIGHTS = NS_DC + "accessRights";
    String REL_ALTERNATE = "alternate";
    String REL_CREATOR = NS_DC + "creator";
    String REL_DESCRIBES = NS_ORE + "describes";
    String REL_HAS_PARTICIPANT = NS_ANDS + "hasParticipant";
    String REL_HAS_OUTPUT = NS_ANDS + "hasOutput";
    String REL_IS_MANAGER_OF = NS_ANDS + "isManagerOf";
    String REL_IS_ACCESSED_VIA = NS_CLD + "isAccessedVia";
    String REL_MADE = NS_FOAF + "made";
    String REL_MBOX = NS_FOAF + "mbox";
    String REL_IS_DESCRIBED_BY = NS_ORE + "isDescribedBy";
    String REL_IS_LOCATED_AT = NS_CLD + "isLocatedAt";
    String REL_IS_MANAGED_BY = NS_ANDS + "isManagedBy";
    String REL_IS_OUTPUT_OF = NS_ANDS + "isOutputOf";
    String REL_IS_REFERENCED_BY = NS_DC + "isReferencedBy";
    String REL_CURRENT_PROJECT = NS_FOAF + "currentProject";
    String REL_IS_SUPPORTED_BY = NS_ANDS + "isSupportedBy";
    String REL_MANAGES_SERVICE = NS_DATASPACE + "managesService";
    String REL_LATEST_VERSION = "latest-version";
    String REL_WORKING_COPY = "working-copy";
    String REL_EDIT = "edit";
    String REL_PAGE = NS_FOAF + "page";
    String REL_PUBLICATIONS = NS_FOAF + "publications";
    String REL_PREDECESSOR_VERSION = "predecessor-version";
    String REL_PUBLISHER = NS_DC + "publisher";
    String REL_RELATED = "related";
    String REL_SELF = "self";
    String REL_LICENSE = "license";
    String REL_SUCCESSOR_VERSION = "successor-version";
    String REL_TEMPORAL = NS_DC + "temporal";
    String REL_ALTERNATIVE = NS_DC + "alternative";
    String REL_SPATIAL = NS_DC + "spatial";
    String REL_VIA = "via";
    String REL_TYPE = NS_RDF_99 + "#type";


    /**
     * term attributes
     */
    String TERM_ANDS_GROUP = "The University of Queensland";
    String TERM_ACTIVITY = NS_FOAF + "Project";
    String TERM_COLLECTION = NS_DCMITYPE + "Collection";
    String TERM_AGENT_AS_GROUP = NS_FOAF + "Group";
    String TERM_AGENT_AS_PERSON = NS_FOAF + "Person";
    String TERM_SERVICE = NS_VIVO + "Service";
    String LABEL_KEYWORD = "keyword";

    String SCHEME_DCMITYPE = NS_DCMITYPE;
    String SCHEME_KEYWORD_SUFFIX = "keyword";
    String SCHEME_ANZSRC_FOR = NS_ANZSRC + "for";
    String SCHEME_ANZSRC_SEO = NS_ANZSRC + "seo";
    String SCHEME_ANZSRC_TOA = NS_ANZSRC + "toa";

    String PROPERTY_TITLE = NS_FOAF + "title";
    String PROPERTY_GIVEN_NAME = NS_FOAF + "givenName";
    String PROPERTY_FAMILY_NAME = NS_FOAF + "familyName";

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
    String SESSION_ATTRIBUTE_CURRENT_EMAIL = "currentEmail";
    String SESSION_ATTRIBUTE_LDAP_CONTEXT = "currentUser";

    /**
     * QNames
     */
    QName QNAME_RDFA_META = new QName(Constants.NS_RDFA, "meta", "rdfa");
    QName QNAME_GEO_RSS_POINT = new QName(Constants.NS_GEORSS, "point", "georss");
    QName QNAME_GEO_RSS_POLYGON = new QName(Constants.NS_GEORSS, "polygon", "georss");

}
