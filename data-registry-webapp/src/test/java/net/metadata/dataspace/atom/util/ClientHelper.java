package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.TestConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 08/11/2010
 * Time: 4:06:52 PM
 */
public class ClientHelper {

	private static ResourceLoader loader = new DefaultResourceLoader();
    private static boolean dataLoaded;

    public ClientHelper() {
        dataLoaded = false;
    }

    public static int login(HttpClient client, String username, String password) throws Exception {
        PostMethod postMethod = new PostMethod(TestConstants.URL_PREFIX + "login?username=" + username + "&password=" + password);
        return client.executeMethod(postMethod);
    }

	public static void login(WebClient client, String username,
			String password) throws FailingHttpStatusCodeException, IOException {
		WebRequest request = new WebRequest(
				new URL(TestConstants.URL_PREFIX + "login?username=" + username + "&password=" + password),
				HttpMethod.POST);
		client.getPage(request);
	}

	public static int reindex(HttpClient client) throws Exception {
        PostMethod postMethod = new PostMethod(TestConstants.URL_PREFIX + "reindex");
        return client.executeMethod(postMethod);
    }

    public static int logout(HttpClient client) throws Exception {
        PostMethod postMethod = new PostMethod(TestConstants.URL_PREFIX + "logout");
        return client.executeMethod(postMethod);
    }

    public static PostMethod postEntry(HttpClient client, String fileName, String path) throws Exception {
        String fullURL = TestConstants.URL_PREFIX + path;
        PostMethod post = new PostMethod(fullURL);
        File inputFile = getFile(fileName);
        RequestEntity entity = new FileRequestEntity(inputFile, TestConstants.ATOM_ENTRY_MIMETYPE);
        post.setRequestEntity(entity);
        client.executeMethod(post);
        return post;
    }

    public static PutMethod putEntry(HttpClient client, String fileName, String uri, String acceptHeader) throws Exception {
        PutMethod putMethod = new PutMethod(uri);
        putMethod.setRequestHeader("Accept", acceptHeader);
        File inputFile = getFile(fileName);
        RequestEntity entity = new FileRequestEntity(inputFile, TestConstants.ATOM_ENTRY_MIMETYPE);
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

    public static File getFile(String fileName) throws IOException {
        return loader.getResource(fileName).getFile();
    }

    public static void loadTestData(HttpClient client) throws Exception {
        if (! dataLoaded) {
            ResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            //authenticate
            int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
            assertEquals("Could not authenticate", 200, status);

            String[] types = {
                    TestConstants.PATH_FOR_AGENTS,
                    TestConstants.PATH_FOR_COLLECTIONS,
                    TestConstants.PATH_FOR_ACTIVITIES,
                    TestConstants.PATH_FOR_SERVICES };
            for (int t = 0; t < types.length; t++) {
                String type = types[t];
                List<String> testEntities = new LinkedList<String>();
                {
                    Resource[] resources = resolver.getResources(
                            "classpath:files/**/"+type+"/*.atom");
                    for (int r = 0; r < resources.length; r++) {
                        Resource resource = resources[r];
                        testEntities.add(resource.getURI().toString());
                    }
                }
                for (String fileName : testEntities) {
                    PostMethod postMethod = ClientHelper.postEntry(client, fileName, type);
                    assertEquals("Could not post entry", 201, postMethod.getStatusCode());
                    postMethod.releaseConnection();
                }
            }
            dataLoaded = true;
        }
    }
}
