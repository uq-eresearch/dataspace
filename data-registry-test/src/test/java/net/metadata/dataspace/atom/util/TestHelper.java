package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import org.apache.abdera.Abdera;
import org.apache.abdera.examples.xsltxpath.XPathExample;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;

import java.io.File;
import java.net.URL;

/**
 * Author: alabri
 * Date: 08/11/2010
 * Time: 4:06:52 PM
 */
public class TestHelper {
    private static Abdera abdera = new Abdera();

    public static int login(HttpClient client, String username, String password) throws Exception {
        PostMethod postMethod = new PostMethod(Constants.URL_PREFIX + "login?username=" + username + "&password=" + password);
        return client.executeMethod(postMethod);
    }

    public static PostMethod postEntry(HttpClient client, String fileName, String pathForActivities) throws Exception {
        String fullURL = Constants.URL_PREFIX + pathForActivities;
        PostMethod post = new PostMethod(fullURL);
        URL resource = XPathExample.class.getResource(fileName);
        String path = resource.toExternalForm();
        File inputFile = new File(path);
        RequestEntity entity = new FileRequestEntity(inputFile, Constants.ATOM_ENTRY_MIMETYPE);
        post.setRequestEntity(entity);
        client.executeMethod(post);
        return post;
    }

    public static PutMethod putEntry(HttpClient client, String fileName, String uri, String acceptHeader) throws Exception {
        PutMethod putMethod = new PutMethod(uri);
        putMethod.setRequestHeader("Accept", acceptHeader);
        URL resource = XPathExample.class.getResource(fileName);
        String path = resource.toExternalForm();
        File inputFile = new File(path);
        RequestEntity entity = new FileRequestEntity(inputFile, Constants.ATOM_ENTRY_MIMETYPE);
        putMethod.setRequestEntity(entity);
        client.executeMethod(putMethod);
        return putMethod;
    }

    public static GetMethod getEntry(HttpClient client, String uri, String acceptHeader) throws Exception {
        GetMethod getMethod = new GetMethod(uri);
        getMethod.setRequestHeader("Accept", acceptHeader);
        client.executeMethod(getMethod);
        return getMethod;
    }

    public static DeleteMethod deleteEntry(HttpClient client, String uri) throws Exception {
        DeleteMethod deleteMethod = new DeleteMethod(uri);
        client.executeMethod(deleteMethod);
        return deleteMethod;
    }
}
