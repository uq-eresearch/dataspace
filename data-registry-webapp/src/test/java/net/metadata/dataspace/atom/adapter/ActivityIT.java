package net.metadata.dataspace.atom.adapter;


import net.metadata.dataspace.app.TestConstants;
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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.InputStream;

import static junit.framework.Assert.*;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 3:08:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = TestConstants.TEST_CONTEXT)
public class ActivityIT {

    public void testActivityCRUD() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        //Get entry
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get entry after post", 200, getMethod.getStatusCode());
        //get first version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get first version of entry after post", 200, getMethod.getStatusCode());
        //Edit Entry
        fileName = "/files/put/update-activity.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not edit entry", 200, putMethod.getStatusCode());
        //get second version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/2", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get second version of entry after edit", 200, getMethod.getStatusCode());
        //Get version history
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get version history", 200, getMethod.getStatusCode());
        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, deleteMethod.getStatusCode());
        //check that entry is deleted
        getMethod = ClientHelper.getEntry(client, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Entry should be GONE", 410, getMethod.getStatusCode());
    }

    @Test
    public void testActivityUnauthorized() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //post without authentication
        String fileName = "/files/post/new-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Posting without authenticating, Wrong status code", 401, postMethod.getStatusCode());

        //login
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //post with authentication
        postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating, Wrong status code", 404, getMethod.getStatusCode());

        //get first version without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get first version without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get working copy without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/working-copy", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get working copy without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get version history without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get version history without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //Edit without authenticating
        fileName = "/files/put/update-activity.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Editing without authenticating, Wrong status code", 401, putMethod.getStatusCode());

        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Deleting without authenticating, Wrong status code", 401, deleteMethod.getStatusCode());
    }

    @Test
    public void testActivityPublishing() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-activity.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating should now return OK", 200, getMethod.getStatusCode());
    }

    @Test
    public void testActivityFeed() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-activity.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        String feedUrl = TestConstants.URL_PREFIX + TestConstants.PATH_FOR_ACTIVITIES;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, TestConstants.ATOM_FEED_MIMETYPE);
        assertEquals("Could not get feed", 200, getMethod.getStatusCode());

    }

    	@Test
        public void testActivityRecordContent() throws Exception {

            //create a client
            HttpClient client = new HttpClient();
            //authenticate
            int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
            assertEquals("Could not authenticate", 200, status);
            //Post Entry
            String fileName = "/files/post/new-activity.xml";
            PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
            assertEquals("Could not post entry", 201, postMethod.getStatusCode());
            String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

            XPath xpath = XPathHelper.getXPath();
            InputStream responseBodyAsStream = postMethod.getResponseBodyAsStream();
            Document docFromStream = XPathHelper.getDocFromStream(responseBodyAsStream);
            Document docFromFile = XPathHelper.getDocFromFile(fileName);

            String id = xpath.evaluate(TestConstants.RECORD_ID_PATH, docFromStream);
            assertNotNull("Entry missing id", id);
            String originalId = xpath.evaluate(TestConstants.RECORD_ID_PATH, docFromFile);
            assertNotNull("Original Entry missing title", originalId);
            assertEquals("Entry's title is incorrect", originalId, id);

            String relDescribes = xpath.evaluate(TestConstants.RECORD_REL_DESCRIBES_PATH, docFromStream);
            assertNotNull("Entry missing \"describes\" relation", relDescribes);
            assertTrue("Entry's \"describes\" relation does not contain path to entry: "+relDescribes, relDescribes.contains(TestConstants.PATH_FOR_ACTIVITIES));

            String title = xpath.evaluate(TestConstants.RECORD_TITLE_PATH, docFromStream);
            assertNotNull("Entry missing title", title);
            String originalTitle = xpath.evaluate(TestConstants.RECORD_TITLE_PATH, docFromFile);
            assertNotNull("Original Entry missing title", originalTitle);
            assertEquals("Entry's title is incorrect", originalTitle, title);

            String content = xpath.evaluate(TestConstants.RECORD_CONTENT_PATH, docFromStream);
            assertNotNull("Entry missing content", content);
            String originalContent = xpath.evaluate(TestConstants.RECORD_CONTENT_PATH, docFromFile);
            assertNotNull("Original Entry missing content", originalContent);
            assertEquals("Entry's content is incorrect", originalContent, content);

            String updated = xpath.evaluate(TestConstants.RECORD_UPDATED_PATH, docFromStream);
            assertNotNull("Entry missing updated", updated);

            String authorName = xpath.evaluate(TestConstants.RECORD_AUTHOR_NAME_PATH, docFromStream);
            assertNotNull("Entry missing author name", authorName);
            String originalAuthorName = xpath.evaluate(TestConstants.RECORD_AUTHOR_NAME_PATH, docFromFile);
            assertNotNull("Original Entry missing author name", originalAuthorName);
            assertEquals("Entry's author name is incorrect", originalAuthorName, authorName);

            String draft = xpath.evaluate(TestConstants.RECORD_DRAFT_PATH, docFromStream);
            assertNotNull("Entry missing draft element", draft);
            assertEquals("Entry's should be draft", "yes", draft);

            //publish entry
            fileName = "/files/put/published-activity.xml";
            PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
            assertEquals("Could not publish entry", 200, putMethod.getStatusCode());
            docFromStream = XPathHelper.getDocFromStream(putMethod.getResponseBodyAsStream());

            draft = xpath.evaluate(TestConstants.RECORD_DRAFT_PATH, docFromStream);
            assertNotNull("Entry missing draft element", draft);
            assertEquals("Entry's should be published", "no", draft);

            Element selfLink = (Element) xpath.evaluate(TestConstants.RECORD_LINK_PATH + "[@rel='self']", docFromStream, XPathConstants.NODE);
            assertNotNull("Entry missing self link", selfLink);
            String entryLocation = selfLink.getAttribute("href");

            Element xhtmlLinkElement = (Element) xpath.evaluate(TestConstants.RECORD_LINK_PATH + "[@type='" + TestConstants.MIME_TYPE_XHTML + "']", docFromStream, XPathConstants.NODE);
            assertNotNull("Entry missing xhtml link", xhtmlLinkElement);
            String xhtmlLink = xhtmlLinkElement.getAttribute("href");
            String expectedXhtmlLink = entryLocation + "?repr=" + TestConstants.MIME_TYPE_XHTML;
            assertEquals(expectedXhtmlLink, xhtmlLink);

            Element rdfLinkElement = (Element) xpath.evaluate(TestConstants.RECORD_LINK_PATH + "[@type='" + TestConstants.MIME_TYPE_RDF + "']", docFromStream, XPathConstants.NODE);
            assertNotNull("Entry missing rdf link", rdfLinkElement);
            String rdfLink = rdfLinkElement.getAttribute("href");
            String expectedRdfLink = entryLocation + "?repr=" + TestConstants.MIME_TYPE_RDF;
            assertEquals(expectedRdfLink, rdfLink);

            Element rifcsLinkElement = (Element) xpath.evaluate(TestConstants.RECORD_LINK_PATH + "[@type='" + TestConstants.MIME_TYPE_XHTML + "']", docFromStream, XPathConstants.NODE);
            assertNotNull("Entry missing rifcs link", rifcsLinkElement);
            String rifcsLink = rifcsLinkElement.getAttribute("href");
            String expectedRifcsLink = entryLocation + "?repr=" + TestConstants.MIME_TYPE_XHTML;
            assertEquals(expectedRifcsLink, rifcsLink);

        }

    @Test
    public void testActivityFeedContent() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, TestConstants.USERNAME, TestConstants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, TestConstants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-activity.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, TestConstants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        String feedUrl = TestConstants.URL_PREFIX + TestConstants.PATH_FOR_ACTIVITIES;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, TestConstants.ATOM_FEED_MIMETYPE);
        assertEquals("Could not get feed", 200, getMethod.getStatusCode());

        XPath xpath = XPathHelper.getXPath();
        InputStream responseBodyAsStream = getMethod.getResponseBodyAsStream();
        Document docFromStream = XPathHelper.getDocFromStream(responseBodyAsStream);

        String id = xpath.evaluate(TestConstants.FEED_ID_PATH, docFromStream);
        assertNotNull("Feed missing id", id);
        assertTrue("Feed's id does not contain path to entry", id.contains(TestConstants.PATH_FOR_ACTIVITIES));

        String title = xpath.evaluate(TestConstants.FEED_TITLE_PATH, docFromStream);
        assertNotNull("Feed missing title", title);
        assertEquals("Feed's title is incorrect", TestConstants.TITLE_FOR_ACTIVITIES, title);

        String updated = xpath.evaluate(TestConstants.FEED_UPDATED_PATH, docFromStream);
        assertNotNull("Feed missing updated", updated);

        String authorName = xpath.evaluate(TestConstants.FEED_AUTHOR_NAME_PATH, docFromStream);
        assertNotNull("Feed missing author name", authorName);

        Element selfLink = (Element) xpath.evaluate(TestConstants.FEED_LINK_PATH + "[@rel='self']", docFromStream, XPathConstants.NODE);
        assertNotNull("Feed missing self link", selfLink);
        String feedSelfLink = selfLink.getAttribute("href");
        assertTrue(feedSelfLink.contains(TestConstants.ATOM_FEED_MIMETYPE));

        Element alternateLink = (Element) xpath.evaluate(TestConstants.FEED_LINK_PATH + "[@rel='alternate']", docFromStream, XPathConstants.NODE);
        assertNotNull("Feed missing alternate link", alternateLink);

        //Number of entries in the feed
        NodeList nodes = (NodeList) xpath.evaluate(TestConstants.FEED_PATH + "/atom:entry", docFromStream, XPathConstants.NODESET);
        int numberOfEntries = nodes.getLength();
        assertTrue("There should be at least one entry in this feed", numberOfEntries > 0);

        Node entry = nodes.item(0);

        String entryId = xpath.evaluate(TestConstants.FEED_PATH + TestConstants.RECORD_ID_PATH, entry);
        assertNotNull("Feed entry missing id", entryId);
        assertTrue("Feed entry's id does not contain path to entry", entryId.contains(TestConstants.PATH_FOR_ACTIVITIES));

        String entryTitle = xpath.evaluate(TestConstants.FEED_PATH + TestConstants.RECORD_TITLE_PATH, entry);
        assertNotNull("Feed entry missing title", entryTitle);
        assertFalse("Feed entry title is empty", entryTitle.isEmpty());

        String entryContent = xpath.evaluate(TestConstants.FEED_PATH + TestConstants.RECORD_CONTENT_PATH, entry);
        assertNotNull("Feed entry missing content", entryContent);
        assertFalse("Feed entry content is empty", entryContent.isEmpty());

        String entryUpdated = xpath.evaluate(TestConstants.FEED_PATH + TestConstants.RECORD_UPDATED_PATH, entry);
        assertNotNull("Feed entry missing updated", entryUpdated);
        assertFalse("Feed entry updated is empty", entryContent.isEmpty());

        String draft = xpath.evaluate(TestConstants.FEED_PATH + TestConstants.RECORD_DRAFT_PATH, entry);
        assertNotNull("Feed Entry missing draft element", draft);
        assertFalse("Feed entry draft is empty", entryContent.isEmpty());

    }


}
