package net.metadata.dataspace.app;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = TestConstants.TEST_CONTEXT)
public class SolrIT {

	private static final String SOLR_CONFIG_DIR = "src/main/resources/";
	private static final Set<String> stopwords = new TreeSet<String>();

    private static Logger logger = Logger.getLogger(SolrIT.class);
	private XPath xpath = XPathHelper.getXPath();

	@BeforeClass
	public static void initStopwords() throws IOException {
		File stopwordsFile = new File(SOLR_CONFIG_DIR+"/stopwords.txt");
        BufferedReader r = new BufferedReader(new FileReader(stopwordsFile));
        String line;
        while ( (line = r.readLine()) != null) {
        	line = line.trim();
        	if (line.startsWith("#") || line.length() == 0)
        		continue;
        	stopwords.add(line);
        }
	}

	@BeforeClass
    public static void loadLotsOfTestData() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

		ClientHelper.loadTestData(client);
        ClientHelper.reindex(client);
    }

	// Test tagcloud for stopwords
	@Test
    public void testTagCloud() throws Exception {
		final int expectedTagCount = 50;

    	String url = TestConstants.URL_PREFIX+"#tags";
        final WebClient webClient = new WebClient();
    	final HtmlPage page = webClient.getPage(url);
    	int tries = 0;
    	NodeList tagNodes = null;
    	do {
    		// Wait for JS to render
        	Thread.sleep(500);
        	// Get the nodes
        	tagNodes = (NodeList) xpath.evaluate(
        			"//div[@id='tags']/div[@id='topics']/a/text()",
        			page, XPathConstants.NODESET);
    	} while (tagNodes.getLength() < expectedTagCount && tries++ < 10);
    	assertEquals("Tag count lower than expected",
    			expectedTagCount, tagNodes.getLength());

    	Set<String> tags = new TreeSet<String>();
    	for (int i = 0; i < tagNodes.getLength(); i++) {
    		String tag = tagNodes.item(i).getTextContent().trim();
    		tags.add(tag);
    	}

    	Set<String> intersection = (new TreeSet<String>(tags));
    	intersection.retainAll(stopwords);
    	assertTrue("Tags should not include stopwords. Stopwords in tags: "+
    			intersection,
    			intersection.size() == 0);

        webClient.closeAllWindows();
	}

	@Test
    public void testSearching() throws Exception {
    	//create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-collection.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_COLLECTIONS);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        postMethod.releaseConnection();
        //publish entry
        fileName = "/files/put/published-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());
        putMethod.releaseConnection();
        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating should now return OK", 200, getMethod.getStatusCode());
        Document doc = XPathHelper.getDocFromStream(getMethod.getResponseBodyAsStream());
        getMethod.releaseConnection();
        String title = xpath.evaluate(TestConstants.RECORD_TITLE_PATH, doc);
        String authorName = xpath.evaluate(TestConstants.RECORD_AUTHOR_NAME_PATH, doc);
        String authorEmail = xpath.evaluate(TestConstants.RECORD_AUTHOR_EMAIL_PATH, doc);

        // Trigger re-index
        ClientHelper.reindex(client);

        // Test searching on title (but skip stopwords)
        {
	        final WebClient webClient = new WebClient();
	        String searchTitle = title.replaceAll("\\W+"," ").trim();
	        for (String word : searchTitle.split(" ")) {
	        	if (stopwords.contains(word)) {
	        		// Skip stopword
	        		continue;
	        	}
	        	String url = TestConstants.URL_PREFIX+"search?q="+word;
	        	final HtmlPage page = webClient.getPage(url);
                assertNotNull("Page is null for: " + url, page);
	        	// Wait for JS to eval
	        	while (!page.asText().contains("displaying"))
		        	Thread.sleep(100);
	        	final String pageAsText = page.asText();
	        	assertTrue(
	        			String.format(
	        				"Cannot find \"%s\" in:\n%s", searchTitle, pageAsText),
	        			pageAsText.contains(searchTitle));
	        }
	        webClient.closeAllWindows();
        }
    }

}
