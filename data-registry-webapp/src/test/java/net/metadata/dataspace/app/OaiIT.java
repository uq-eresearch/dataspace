package net.metadata.dataspace.app;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.metadata.dataspace.atom.util.ClientHelper;
import net.metadata.dataspace.atom.util.XPathHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = TestConstants.TEST_CONTEXT)

/**
 * Created by IntelliJ IDEA.
 * User: nigelward
 * Date: 16/12/11
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class OaiIT {
    private static final String SOLR_CONFIG_DIR = "src/main/resources/";

    private static Logger logger = Logger.getLogger(SolrIT.class);
    private XPath xpath = XPathHelper.getXPath();

    @BeforeClass
    public static void loadTestData() throws Exception {
        ResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        //create a client
        HttpClient client = new HttpClient();
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
                logger.info("Loading test data from "+fileName);
                PostMethod postMethod = ClientHelper.postEntry(client, fileName, type);
                assertEquals("Could not post entry", 201, postMethod.getStatusCode());
                postMethod.releaseConnection();
            }
        }

    }

    @Test
    public void testOaiIdentify() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, TestConstants.URL_PREFIX + "oai?verb=Identify", TestConstants.MIME_TYPE_OAI);
        assertEquals("Could not get OAI Identify", 200, getMethod.getStatusCode());

        // check response
        Document doc = XPathHelper.getDocFromStream(getMethod.getResponseBodyAsStream());
        getMethod.releaseConnection();
        String baseURL = xpath.evaluate(TestConstants.OAI_IDENTIFY_REPO_BASE_PATH, doc);
        assertEquals("Incorrect OAI Identify baseURL", TestConstants.URL_PREFIX + "oai", baseURL);
    }

    @Test
    public void testOaiEmptyList() throws Exception {
        // OAI request for records from 1999.  Should return no records
        String in1999 = "oai?verb=ListRecords&from=1999-12-10T00:00:01Z&until=1999-12-17T03:07:39Z&metadataPrefix=rif";

        //create a client
        HttpClient client = new HttpClient();

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, TestConstants.URL_PREFIX + in1999, TestConstants.MIME_TYPE_OAI);
        assertEquals("Could not get OAI ListRecords", 200, getMethod.getStatusCode());

        // check response
        Document doc = XPathHelper.getDocFromStream(getMethod.getResponseBodyAsStream());
        getMethod.releaseConnection();
        String code = xpath.evaluate(TestConstants.OAI_LISTRECORDS_ERROR_PATH, doc);
        assertEquals("Incorrect OAI Identify baseURL", "noRecordsMatch", code);
    }

    @Test
    public void testOaiListRecords() throws Exception {
        // OAI request for records from 2011 to now.  Should records
        String from2011 = "oai?verb=ListRecords&from=2011-01-01T00:00:01Z&metadataPrefix=rif";

        //create a client
        HttpClient client = new HttpClient();

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, TestConstants.URL_PREFIX + from2011, TestConstants.MIME_TYPE_OAI);
        assertEquals("Could not get OAI ListRecords", 200, getMethod.getStatusCode());

        // check response
        Document doc = XPathHelper.getDocFromStream(getMethod.getResponseBodyAsStream());
        getMethod.releaseConnection();
        String records = xpath.evaluate(TestConstants.OAI_LISTRECORDS_SUCCESS_PATH, doc);
    }

}

