package net.metadata.dataspace.app;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;

import javax.xml.namespace.QName;
import java.util.UUID;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 2:14:07 PM
 */
public class DataRegistryClient {
    public static void main(String[] args) throws Exception {
        // Set the introspection document
        String start = "http://localhost:9002";

        Abdera abdera = new Abdera();

        // Initialize the client and set the auth credentials
        AbderaClient abderaClient = new AbderaClient(abdera);

        // Get the service doc and locate the href of the collection
        Document<Service> service_doc = abderaClient.get(start).getDocument();
        Service service = service_doc.getRoot();
        Collection collection = service.getWorkspaces().get(0).getCollections().get(0);
        String uri = collection.getHref().toString();

        // Prepare the entry
        Factory factory = abdera.getFactory();


        Entry entry = factory.newEntry();
        entry.setId(FOMHelper.generateUuid());
        entry.setUpdated(new java.util.Date());
        entry.addAuthor("Abdul Alabri");
        entry.addAuthor("Nigel Ward");
        entry.setTitle("New Party");
        entry.setSummary("This is a description of the party");
        entry.setContent("Optional Content");
        // Add the Specific extensions
        ((Element) entry.addExtension(new QName("http://http://www.w3.org/2005/Atom", "collectorOf"))).setAttributeValue("value", UUID.randomUUID().toString());
//        entry.setContent(new Element("collectorOf"), "");

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        Response response = abderaClient.post(start + uri, entry, options);


        // Check the response
        if (response.getStatus() == 201) {
            System.out.println("Success!");
        } else {
            System.out.println("Failed! " + response.getStatus());
        }
        System.out.println(entry.toString());
    }
}
