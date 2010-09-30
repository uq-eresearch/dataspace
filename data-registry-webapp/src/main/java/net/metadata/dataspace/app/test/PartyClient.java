package net.metadata.dataspace.app.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.log4j.Logger;

import java.util.Random;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 2:14:07 PM
 */
public class PartyClient {

    private static Logger logger = Logger.getLogger(PartyClient.class);

    public static void main(String[] args) throws Exception {

        //You need to make sure your server is running
        // Set the introspection document
        String host = "http://localhost:8080";
        String base = "/party/";
        String partyService = host + base;

        Abdera abdera = new Abdera();

        // Initialize the client and set the auth credentials
        AbderaClient abderaClient = new AbderaClient(abdera);

        // Get the service doc and locate the href of the collection
        Document<Service> service_doc = abderaClient.get(partyService).getDocument();
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
        String name = "New Party " + new Random().nextInt(5000);
        entry.setTitle(name);
        entry.setSummary("This is a description of " + name);
//        entry.setContent("Optional Content");
        // Add the Specific extensions
//        ((Element) entry.addExtension(new QName("http://http://www.w3.org/2005/Atom", "collectorOf"))).setAttributeValue("value", UUID.randomUUID().toString());
//        entry.setContent(new Element("collectorOf"), "");

        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);

        // Post the entry
        String fullURL = host + uri;
        Response response = abderaClient.post(fullURL, entry, options);


        // Check the response
        logger.info("Posting Entry:\n" + entry.toString());
        if (response.getStatus() == 201) {
            logger.info("Success! Party Created.");
        } else {
            logger.warn("Failed! " + "\n" + response.getStatus() + " " + response.getStatusText());
        }
    }
}
