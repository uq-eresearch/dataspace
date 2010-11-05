package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;

import java.util.Random;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 3:08:31 PM
 */
public class ActivityAdapterTest extends DataCollectionsRegistryTestCase {

    public void testPostActivity() throws Exception {

        //You need to make sure your server is running
        // Set the introspection document
        String host = Constants.URL_PREFIX;
        String base = Constants.PATH_FOR_ACTIVITIES;
        String serviceURI = host + "registry.atomsvc";

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


        Entry entry = factory.newEntry();
        entry.setId(FOMHelper.generateUuid());
        entry.setUpdated(new java.util.Date());
        entry.addAuthor("Abdul Alabri");
        entry.addAuthor("Nigel Ward");
        String name = "New Activity " + new Random().nextInt(5000);
        entry.setTitle(name);
        entry.setSummary("This is a description of " + name);
        entry.setContent("This is content of " + name);

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        String fullURL = host + base;
        Response response = abderaClient.post(fullURL, entry, options);

        int status = response.getStatus();
        assertTrue("Could not post entry, The server returned: " + response.getStatus(), status == 201);

    }
}
