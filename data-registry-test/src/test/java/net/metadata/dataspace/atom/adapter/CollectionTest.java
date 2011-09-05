package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.ClientHelper;
import net.metadata.dataspace.atom.util.XPathHelper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import static junit.framework.Assert.*;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class CollectionTest {

	@Test
    public void testCollectionCRUD() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-collection.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
        assertEquals(postMethod.getResponseBodyAsString(), 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        //Get entry
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get entry after post", 200, getMethod.getStatusCode());
        //get first version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get first version of entry after post", 200, getMethod.getStatusCode());
        //Edit Entry
        fileName = "/files/put/update-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not edit entry", 200, putMethod.getStatusCode());
        //get second version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/2", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get second version of entry after edit", 200, getMethod.getStatusCode());
        //Get version history
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get version history", 200, getMethod.getStatusCode());
        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, deleteMethod.getStatusCode());
        //check that entry is deleted
        getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Entry should be GONE", 410, getMethod.getStatusCode());
    }

    @Test
    public void testCollectionUnauthorized() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //post without authentication
        String fileName = "/files/post/new-collection.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
        assertEquals("Posting without authenticating, Wrong status code", 401, postMethod.getStatusCode());

        //login
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //post with authentication
        postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating, Wrong status code", 404, getMethod.getStatusCode());

        //get first version without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get first version without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get working copy without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/working-copy", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get working copy without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get version history without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get version history without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //Edit without authenticating
        fileName = "/files/put/update-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Editing without authenticating, Wrong status code", 401, putMethod.getStatusCode());

        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Deleting without authenticating, Wrong status code", 401, deleteMethod.getStatusCode());
    }

    @Test
    public void testCollectionPublishing() throws Exception {
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
        getMethod.releaseConnection();
    }

    @Test
    public void testCollectionFeed() throws Exception {
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

        //publish entry
        fileName = "/files/put/published-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        String feedUrl = Constants.URL_PREFIX + Constants.PATH_FOR_COLLECTIONS;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, Constants.ATOM_FEED_MIMETYPE);
        assertEquals("Get Could not get feed", 200, getMethod.getStatusCode());

    }

    @Test
    public void testCollectionRecordContent() throws Exception {

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

        XPath xpath = XPathHelper.getXPath();
        InputStream responseBodyAsStream = postMethod.getResponseBodyAsStream();
        Document docFromStream = XPathHelper.getDocFromStream(responseBodyAsStream);

        Document docFromFile = XPathHelper.getDocFromFile(fileName);

        String id = xpath.evaluate(Constants.RECORD_ID_PATH, docFromStream);
        assertNotNull("Entry missing id", id);
        assertTrue("Entry's id does not contain path to entry", id.contains(Constants.PATH_FOR_COLLECTIONS));

        String title = xpath.evaluate(Constants.RECORD_TITLE_PATH, docFromStream);
        assertNotNull("Entry missing title", title);
        String originalTitle = xpath.evaluate(Constants.RECORD_TITLE_PATH, docFromFile);
        assertNotNull("Original Entry missing title", originalTitle);
        assertEquals("Entry's title is incorrect", originalTitle, title);

        String content = xpath.evaluate(Constants.RECORD_CONTENT_PATH, docFromStream);
        assertNotNull("Entry missing content", content);
        String originalContent = xpath.evaluate(Constants.RECORD_CONTENT_PATH, docFromFile);
        assertNotNull("Original Entry missing content", originalContent);
        assertEquals("Entry's content is incorrect", originalContent, content);

        String updated = xpath.evaluate(Constants.RECORD_UPDATED_PATH, docFromStream);
        assertNotNull("Entry missing updated", updated);

        // Note: we check the emails, because we've no guarantee that the names are the same.
        assertXPathSameTextContent(Constants.RECORD_AUTHOR_EMAIL_PATH,
        		docFromStream, docFromFile);

        String draft = xpath.evaluate(Constants.RECORD_DRAFT_PATH, docFromStream);
        assertNotNull("Entry missing draft element", draft);
        assertEquals("Entry's should be draft", "yes", draft);

        String spatialPath = Constants.RECORD_LINK_PATH + "[@rel='http://purl.org/dc/terms/spatial']";
        Element spatialLink = (Element) xpath.evaluate(spatialPath, docFromStream, XPathConstants.NODE);
        assertNotNull("Entry missing spatial rel link", spatialLink);
        assertXPathSameTextContent(spatialPath+"/@href",
        		docFromFile, docFromStream);

        //publish entry
        fileName = "/files/put/published-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());
        docFromStream = XPathHelper.getDocFromStream(putMethod.getResponseBodyAsStream());

        draft = xpath.evaluate(Constants.RECORD_DRAFT_PATH, docFromStream);
        assertNotNull("Entry missing draft element", draft);
        assertEquals("Entry's should be published", "no", draft);

        Element selfLink = (Element) xpath.evaluate(Constants.RECORD_LINK_PATH + "[@rel='self']", docFromStream, XPathConstants.NODE);
        assertNotNull("Entry missing self link", selfLink);
        String entryLocation = selfLink.getAttribute("href");

        Element xhtmlLinkElement = (Element) xpath.evaluate(Constants.RECORD_LINK_PATH + "[@type='" + Constants.MIME_TYPE_XHTML + "']", docFromStream, XPathConstants.NODE);
        assertNotNull("Entry missing xhtml link", xhtmlLinkElement);
        String xhtmlLink = xhtmlLinkElement.getAttribute("href");
        String expectedXhtmlLink = entryLocation + "?repr=" + Constants.MIME_TYPE_XHTML;
        assertEquals(expectedXhtmlLink, xhtmlLink);

        Element rdfLinkElement = (Element) xpath.evaluate(Constants.RECORD_LINK_PATH + "[@type='" + Constants.MIME_TYPE_RDF + "']", docFromStream, XPathConstants.NODE);
        assertNotNull("Entry missing rdf link", rdfLinkElement);
        String rdfLink = rdfLinkElement.getAttribute("href");
        String expectedRdfLink = entryLocation + "?repr=" + Constants.MIME_TYPE_RDF;
        assertEquals(expectedRdfLink, rdfLink);

        Element rifcsLinkElement = (Element) xpath.evaluate(Constants.RECORD_LINK_PATH + "[@type='" + Constants.MIME_TYPE_XHTML + "']", docFromStream, XPathConstants.NODE);
        assertNotNull("Entry missing rifcs link", rifcsLinkElement);
        String rifcsLink = rifcsLinkElement.getAttribute("href");
        String expectedRifcsLink = entryLocation + "?repr=" + Constants.MIME_TYPE_XHTML;
        assertEquals(expectedRifcsLink, rifcsLink);

        // Source author should be the one specified in the POST
        if (hasXPathMatch(Constants.RECORD_SOURCE_AUTHOR_NAME_PATH, docFromFile)) {
        	// Check that the authors match
	        assertXPathSameTextContent(Constants.RECORD_SOURCE_AUTHOR_NAME_PATH+"/text()",
	        		docFromFile, docFromStream);
        } else {
        	// The current user should have been used as the source instead
        	Element authorEmailNode = (Element) xpath.evaluate(Constants.RECORD_SOURCE_AUTHOR_NAME_PATH, docFromStream, XPathConstants.NODE);
            assertNotNull("Entry missing source author", authorEmailNode);
            assertEquals("Source author has unexpected email", authorEmailNode.getTextContent(), "Abdul Alabri");
        }

    }

	@Test
    public void testCollectionFeedContent() throws Exception {
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

        //publish entry
        fileName = "/files/put/published-collection.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        String feedUrl = Constants.URL_PREFIX + Constants.PATH_FOR_COLLECTIONS;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, Constants.ATOM_FEED_MIMETYPE);
        assertEquals("Could not get feed", 200, getMethod.getStatusCode());

        XPath xpath = XPathHelper.getXPath();
        InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();
        Document docFromStream = XPathHelper.getDocFromStream(responseBodyAsStream);

        String id = xpath.evaluate(Constants.FEED_ID_PATH, docFromStream);
        assertNotNull("Feed missing id", id);
        assertTrue("Feed's id does not contain path to entry", id.contains(Constants.PATH_FOR_COLLECTIONS));

        String title = xpath.evaluate(Constants.FEED_TITLE_PATH, docFromStream);
        assertNotNull("Feed missing title", title);
        assertEquals("Feed's title is incorrect", Constants.TITLE_FOR_COLLECTIONS, title);

        String updated = xpath.evaluate(Constants.FEED_UPDATED_PATH, docFromStream);
        assertNotNull("Feed missing updated", updated);

        String authorName = xpath.evaluate(Constants.FEED_AUTHOR_NAME_PATH, docFromStream);
        assertNotNull("Feed missing author name", authorName);

        Element selfLink = (Element) xpath.evaluate(Constants.FEED_LINK_PATH + "[@rel='self']", docFromStream, XPathConstants.NODE);
        assertNotNull("Feed missing self link", selfLink);
        String feedSelfLink = selfLink.getAttribute("href");
        assertTrue(feedSelfLink.contains(Constants.ATOM_FEED_MIMETYPE));

        Element alternateLink = (Element) xpath.evaluate(Constants.FEED_LINK_PATH + "[@rel='alternate']", docFromStream, XPathConstants.NODE);
        assertNotNull("Feed missing alternate link", alternateLink);

        //Number of entries in the feed
        NodeList nodes = (NodeList) xpath.evaluate(Constants.FEED_PATH + "/atom:entry", docFromStream, XPathConstants.NODESET);
        int numberOfEntries = nodes.getLength();
        assertTrue("There should be at least one entry in this feed", numberOfEntries > 0);

        Node entry = nodes.item(0);

        String entryId = xpath.evaluate(Constants.FEED_PATH + Constants.RECORD_ID_PATH, entry);
        assertNotNull("Feed entry missing id", entryId);
        assertTrue("Feed entry's id does not contain path to entry", entryId.contains(Constants.PATH_FOR_COLLECTIONS));

        String entryTitle = xpath.evaluate(Constants.FEED_PATH + Constants.RECORD_TITLE_PATH, entry);
        assertNotNull("Feed entry missing title", entryTitle);
        assertFalse("Feed entry title is empty", entryTitle.isEmpty());

        String entryContent = xpath.evaluate(Constants.FEED_PATH + Constants.RECORD_CONTENT_PATH, entry);
        assertNotNull("Feed entry missing content", entryContent);
        assertFalse("Feed entry content is empty", entryContent.isEmpty());

        String entryUpdated = xpath.evaluate(Constants.FEED_PATH + Constants.RECORD_UPDATED_PATH, entry);
        assertNotNull("Feed entry missing updated", entryUpdated);
        assertFalse("Feed entry updated is empty", entryContent.isEmpty());

        String draft = xpath.evaluate(Constants.FEED_PATH + Constants.RECORD_DRAFT_PATH, entry);
        assertNotNull("Feed Entry missing draft element", draft);
        assertFalse("Feed entry draft is empty", entryContent.isEmpty());

    }

    private boolean hasXPathMatch(String xpathExpression, Document doc) throws XPathExpressionException {
    	XPath xpath = XPathHelper.getXPath();
    	NodeList matches = (NodeList) xpath.evaluate(xpathExpression, doc, XPathConstants.NODESET);
    	return matches.getLength() > 0;
    }

    private void assertXPathSameTextContent(String xpathExpression,
    	Document expected, Document actual) throws XPathExpressionException  {
    	XPath xpath = XPathHelper.getXPath();
    	NodeList expectedNodes = (NodeList) xpath.evaluate(
				xpathExpression, expected, XPathConstants.NODESET);
    	NodeList actualNodes = (NodeList) xpath.evaluate(
    				xpathExpression, actual, XPathConstants.NODESET);
        assertEquals("Number of entries don't match for "+xpathExpression,
        		expectedNodes.getLength(), actualNodes.getLength());

        switch (actualNodes.getLength()) {
        	case 0:
        		return;
        	case 1:
        		assertEquals("Content doesn't match for "+xpathExpression,
        				expectedNodes.item(0).getTextContent(),
        				actualNodes.item(0).getTextContent());
        		return;
        	default:
        		// continue
        }
        Set<String> expectedSet = new HashSet<String>();
        Set<String> actualSet = new HashSet<String>();
        for (int i = 0; i < expectedNodes.getLength(); i++) {
        	expectedSet.add(expectedNodes.item(i).getTextContent());
        	actualSet.add(actualNodes.item(i).getTextContent());
        }
        assertEquals("Content doesn't match for "+xpathExpression, expectedSet, actualSet );
    }

    @SuppressWarnings("unused")
	private String getXPathContent(String xpathExpression, Document doc)
    		throws TransformerException, XPathExpressionException
    {
    	XPath xpath = XPathHelper.getXPath();
    	NodeList nl = (NodeList) xpath.evaluate(xpathExpression, doc, XPathConstants.NODESET);
    	StringWriter sw = new StringWriter();
    	for (int i = 0; i < nl.getLength(); i++) {
    		Node node = nl.item(i);
	    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        StreamResult outputTarget = new StreamResult(sw);
	        transformer.transform(new DOMSource(node), outputTarget);
    	}
    	return sw.toString();
    }

}
