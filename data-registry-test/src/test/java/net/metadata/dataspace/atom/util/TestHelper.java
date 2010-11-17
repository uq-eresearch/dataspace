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

    public static Response postEntry(String fileName, String pathForActivities) {
        Abdera abdera = new Abdera();
        AbderaClient abderaClient = new AbderaClient(abdera);
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

    public static Response putEntry(String fileName, String uri) {
        Abdera abdera = new Abdera();
        AbderaClient abderaClient = new AbderaClient(abdera);
        InputStream in = XPathExample.class.getResourceAsStream(fileName);
        Parser parser = abdera.getParser();
        Document doc = parser.parse(in);
        Entry entry = (Entry) doc.getRoot();
        RequestOptions options = abderaClient.getDefaultRequestOptions();
        options.setUseChunked(false);
        Response response = abderaClient.put(uri, entry, options);
        return response;
    }

    public static ClientResponse getEntry(String uri) {
        Abdera abdera = new Abdera();
        AbderaClient abderaClient = new AbderaClient(abdera);
        ClientResponse clientResponse = abderaClient.get(uri);
        return clientResponse;
    }

    public static ClientResponse deleteEntry(String uri) {
        Abdera abdera = new Abdera();
        AbderaClient abderaClient = new AbderaClient(abdera);
        ClientResponse clientResponse = abderaClient.delete(uri);
        return clientResponse;
    }


}