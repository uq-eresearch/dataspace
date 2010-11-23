package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import org.apache.abdera.Abdera;
import org.apache.abdera.examples.xsltxpath.XPathExample;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

import java.io.InputStream;

/**
 * Author: alabri
 * Date: 08/11/2010
 * Time: 4:06:52 PM
 */
public class TestHelper {

    public static AbderaClient login(String username, String password) throws Exception {
        Abdera abdera = new Abdera();
        AbderaClient client = new AbderaClient(abdera);
        client.get(Constants.URL_PREFIX + "login?username=" + username + "&password=" + password);
        return client;
    }

    public static Response postEntry(AbderaClient abderaClient, String fileName, String pathForActivities) {
        Abdera abdera = new Abdera();
        InputStream in = XPathExample.class.getResourceAsStream(fileName);
        Parser parser = abdera.getParser();
        Document doc = parser.parse(in);
        Entry entry = (Entry) doc.getRoot();
        RequestOptions options = abderaClient.getDefaultRequestOptions();

        options.setUseChunked(false);
        String fullURL = Constants.URL_PREFIX + pathForActivities;
        Response response = abderaClient.post(fullURL, entry, options);
        return response;
    }

    public static Response putEntry(AbderaClient abderaClient, String fileName, String uri) {
        Abdera abdera = new Abdera();
        InputStream in = XPathExample.class.getResourceAsStream(fileName);
        Parser parser = abdera.getParser();
        Document doc = parser.parse(in);
        Entry entry = (Entry) doc.getRoot();
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);
        Response response = abderaClient.put(uri, entry, options);
        return response;
    }

    public static ClientResponse getEntry(AbderaClient abderaClient, String uri) {
//        Abdera abdera = new Abdera();
//        AbderaClient abderaClient = new AbderaClient(abdera);
        ClientResponse clientResponse = abderaClient.get(uri);
        return clientResponse;
    }

    public static ClientResponse deleteEntry(AbderaClient abderaClient, String uri) {
//        Abdera abdera = new Abdera();
        ClientResponse clientResponse = abderaClient.delete(uri);
        return clientResponse;
    }


}
