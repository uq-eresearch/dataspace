package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;

/**
 * Author: alabri
 * Date: 08/11/2010
 * Time: 4:06:52 PM
 */
public class ClientHelper {
	
	private static ResourceLoader loader = new DefaultResourceLoader();

    public ClientHelper() {
    }

    public static int login(HttpClient client, String username, String password) throws Exception {
        PostMethod postMethod = new PostMethod(Constants.URL_PREFIX + "login?username=" + username + "&password=" + password);
        return client.executeMethod(postMethod);
    }

    public static int logout(HttpClient client) throws Exception {
        PostMethod postMethod = new PostMethod(Constants.URL_PREFIX + "logout");
        return client.executeMethod(postMethod);
    }

    public static PostMethod postEntry(HttpClient client, String fileName, String path) throws Exception {
        String fullURL = Constants.URL_PREFIX + path;
        PostMethod post = new PostMethod(fullURL);
        File inputFile = getFile(fileName);
        RequestEntity entity = new FileRequestEntity(inputFile, Constants.ATOM_ENTRY_MIMETYPE);
        post.setRequestEntity(entity);
        client.executeMethod(post);
        return post;
    }

    public static PutMethod putEntry(HttpClient client, String fileName, String uri, String acceptHeader) throws Exception {
        PutMethod putMethod = new PutMethod(uri);
        putMethod.setRequestHeader("Accept", acceptHeader);
        File inputFile = getFile(fileName);
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
    
    public static GetMethod reindexSolr(HttpClient client) throws Exception {
    	GetMethod getMethod = new GetMethod(Constants.URL_PREFIX+"solr/dataimport?command=full-import");
        client.executeMethod(getMethod);
        return getMethod;
    }
    
    private static File getFile(String fileName) throws IOException {
        return loader.getResource(fileName).getFile();
    }
    
}
