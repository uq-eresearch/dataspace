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
    String UQ_DATA_COLLECTIONS_REGISTRY_NS = "http://dataspace.metadata.net/";
    String UQ_DATA_COLLECTIONS_REGISTRY_PFX = "uqdata";

    /**
     * Adapters
     */
    String PARTIES_PATH = "parties";
    String COLLECTIONS_PATH = "collections";
    String ACTIVITIES_PATH = "activities";
    String SERVICES_PATH = "services";

    /**
     * Titles
     */
    String PARTIES_TITLE = "Parties";
    String COLLECTIONS_TITLE = "Collections";
    String ACTIVITIES_TITLE = "Activities";
    String SERVICES_TITLE = "Services";

    String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();
    QName SUBJECT_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "subject", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName COLLECTOR_OF_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "collectorOf", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName SUPPORTED_BY_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "supportedBy", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName HAS_OUTPUT_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "hasOutput", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName HAS_PARTICIPANT_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "hasParticipant", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName COLLECTOR_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "collector", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    QName LOCATION_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "location", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);

}
