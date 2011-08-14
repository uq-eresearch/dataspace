package net.metadata.dataspace.app;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class SolrTest {
	
	private static final String SOLR_CONFIG_DIR = "target/solr-config/WEB-INF/classes/";

	private static final Set<String> stopwords = new TreeSet<String>();
	
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

	@Before
    public void loadLotsOfTestData() throws Exception {
    	//create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
                
        List<String> testAgents;
        {
	        String[] tempStrs = {
	    	        "files/carter-brown/toni-johnson-woods.atom",
	    	        "files/crocs-acoustic/craig-franklin.atom",
	    	        "files/crocs-acoustic/hamish-campbell.atom",
	    	        "files/geochem-geochron/maria-mostert.atom",
	    	        "files/geochem-geochron/paulo-vasconcelos.atom",
	    	        "files/glow-worms/david-merritt.atom",
	    	        "files/spatial-social/bob-stimson.atom",
	    	        "files/spatial-social/paul-shyy.atom"
		    };
	        testAgents = Arrays.asList(tempStrs);
        }
        for (String fileName : testAgents) {
        	PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_AGENTS);
            assertEquals("Could not post entry", 201, postMethod.getStatusCode());
            postMethod.releaseConnection();
        }
        
        List<String> testCollections;
        {
	        String[] tempStrs = {
	              "files/carter-brown/carter-brown-covers.atom",
	              "files/carter-brown/pulp-fiction-collection.atom",
	              "files/crocs-acoustic/crocs-acoustic.atom",
	              "files/geochem-geochron/collection-geochemchron.atom",
	              "files/glow-worms/collection-glowworms.atom",
	              "files/spatial-social/census2006-voting2007.atom"
	        };
	        testCollections = Arrays.asList(tempStrs);
        }
        for (String fileName : testCollections) {
        	PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
            assertEquals("Could not post entry", 201, postMethod.getStatusCode());
            postMethod.releaseConnection();
        }
        
        List<String> testActivities;
        {
	        String[] tempStrs = {
	              "files/carter-brown/activity-Aus-popular-fictions.atom",
	              "files/crocs-acoustic/activity-croc-3D.atom",
	              "files/crocs-acoustic/activity-croc-monitor.atom",
	              "files/geochem-geochron/activity-qld-geochemchron.atom",
	              "files/glow-worms/activity-distance-ed.atom",
	              "files/glow-worms/activity-gloworm-tourism.atom",
	              "files/spatial-social/activity-RNSISS.atom",
	              "files/spatial-social/activity-SISSR-Facility.atom"
	        };
	        testActivities = Arrays.asList(tempStrs);
        }
        for (String fileName : testActivities) {
        	PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_ACTIVITIES);
            assertEquals("Could not post entry", 201, postMethod.getStatusCode());
            postMethod.releaseConnection();
        }
        
        List<String> testServices;
        {
	        String[] tempStrs = {
	              "files/spatial-social/service-voting-patterns.atom"
	        };
	        testServices = Arrays.asList(tempStrs);
        }
        for (String fileName : testServices) {
        	PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
            assertEquals("Could not post entry", 201, postMethod.getStatusCode());
            postMethod.releaseConnection();
        }
	}

	@Test
    public void testTagCloud() throws Exception {
		final int expectedTagCount = 50;
		
    	//create a client
        HttpClient client = new HttpClient();
        
        // Force re-index
        GetMethod getMethod = ClientHelper.reindexSolr(client);
        assertEquals("Solr failed to index successfully.", 200, getMethod.getStatusCode());
        getMethod.releaseConnection();
        
		// Test tagcloud for stopwords
        {
        	String url = Constants.URL_PREFIX+"#tags";
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
        	
        	assertTrue("Tags should not include stopwords.",
        			Collections.disjoint(tags, stopwords));
        }
        
        
	}
	
	@Test
    public void testSearching() throws Exception {
    	//create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-collection.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        postMethod.releaseConnection();
        //publish entry
        fileName = "/files/put/published-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());
        putMethod.releaseConnection();
        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating should now return OK", 200, getMethod.getStatusCode());
        Document doc = XPathHelper.getDocFromStream(getMethod.getResponseBodyAsStream());
        getMethod.releaseConnection();
        String title = xpath.evaluate(Constants.RECORD_TITLE_PATH, doc);
        
        // Force re-index
        getMethod = ClientHelper.reindexSolr(client);
        assertEquals("Solr failed to index successfully.", 200, getMethod.getStatusCode());
        getMethod.releaseConnection();

        // Test searching (but skip stopwords)
        {
	        final WebClient webClient = new WebClient();
	        String searchTitle = title.replaceAll("\\W+"," ").trim();
	        for (String word : searchTitle.split(" ")) {
	        	if (stopwords.contains(word)) {
	        		// Skip stopword
	        		continue;
	        	}
	        	String url = Constants.URL_PREFIX+"search?q="+word;
	        	final HtmlPage page = webClient.getPage(url);
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
