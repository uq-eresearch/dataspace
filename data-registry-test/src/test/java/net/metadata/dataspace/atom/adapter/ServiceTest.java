package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import org.apache.abdera.Abdera;
import org.apache.abdera.examples.xsltxpath.XPathExample;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;

import java.io.InputStream;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
public class ServiceTest extends DataCollectionsRegistryTestCase {

    public void testPostService() throws Exception {
        //You need to make sure your server is running
        // Set the introspection document
        String host = Constants.URL_PREFIX;
        String pathForActivities = Constants.PATH_FOR_SERVICES;
        String serviceURI = host + Constants.PATH_FOR_ATOM_SERVICE;

        Abdera abdera = new Abdera();

        // Initialize the client and set the auth credentials
        AbderaClient abderaClient = new AbderaClient(abdera);

        // Get the service doc and locate the href of the collection
        Document service_doc = abderaClient.get(serviceURI).getDocument();
        Service service = (Service) service_doc.getRoot();
        Workspace workspace = (Workspace) service.getWorkspaces().get(0);
        Collection collection = (Collection) workspace.getCollections().get(0);

        // Prepare the entry
        Factory factory = abdera.getFactory();
        InputStream in = XPathExample.class.getResourceAsStream("/files.post/new-service.xml");
        Parser parser = abdera.getParser();
        Document doc = parser.parse(in);
        Entry entry = (Entry) doc.getRoot();

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        String fullURL = host + pathForActivities;
        Response response = abderaClient.post(fullURL, entry, options);

        int status = response.getStatus();
        assertTrue("Could not post entry, The server returned: " + response.getStatus(), status == 201);

    }
}

