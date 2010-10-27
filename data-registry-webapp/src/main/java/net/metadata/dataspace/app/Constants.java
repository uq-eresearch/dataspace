package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 8:16:32 AM
 */
public interface Constants {

    /*
    * Mime types
    */
    String JSON_MIMETYPE = "application/json";
    String ATOM_MIMETYPE = "application/atom+xml";
    String ATOM_ENTRY_MIMETYPE = "application/atom+xml;type=entry";
    String ATOM_FEED_MIMETYPE = "application/atom+xml;type=feed";
    String HTML_MIME_TYPE = "text/html";
    String ATOM_SERVICE_MIMETYPE = "application/atomsvc+xml";


    /*
    * Namespace related
    */
    String UQ_DATA_COLLECTIONS_REGISTRY_NS = "http://dataspace.metadata.net/";
    String UQ_DATA_COLLECTIONS_REGISTRY_PFX = "uqdata";

    /*
    * Adapters
    */
    String PARTIES_PATH = "parties";
    String COLLECTIONS_PATH = "collections";
    String ACTIVITIES_PATH = "activities";
    String SERVICES_PATH = "services";
}
