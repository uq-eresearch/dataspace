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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.InputStream;

import static junit.framework.Assert.*;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:34 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class PartyTest {

    @Test
    public void testPartyCRUD() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-party.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        //Get entry
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get entry after post", 200, getMethod.getStatusCode());
        //get first version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get first version of entry after post", 200, getMethod.getStatusCode());
        //get working copy
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/working-copy", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get working copy after post", 200, getMethod.getStatusCode());
        //Edit Entry
        fileName = "/files/put/update-party.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not edit entry", 200, putMethod.getStatusCode());
        //get second version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/2", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get second version of entry after edit", 200, getMethod.getStatusCode());
        //Get version history
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not version history", 200, getMethod.getStatusCode());
        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, deleteMethod.getStatusCode());
        //check that entry is deleted
        getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Entry should be GONE", 410, getMethod.getStatusCode());
    }

    @Test
    public void testPartyUnauthorized() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //post without authentication
        String fileName = "/files/post/new-party.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Posting without authenticating, Wrong status code", 401, postMethod.getStatusCode());

        //login
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //post with authentication
        postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
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
        fileName = "/files/put/update-party.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Editing without authenticating, Wrong status code", 401, putMethod.getStatusCode());

        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Deleting without authenticating, Wrong status code", 401, deleteMethod.getStatusCode());
    }

    @Test
    public void testPartyPublishing() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-party.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-party.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating should now return OK", 200, getMethod.getStatusCode());
    }

    @Test
    public void testPartyFeed() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-party.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-party.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        String feedUrl = Constants.URL_PREFIX + Constants.PATH_FOR_PARTIES;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, Constants.ATOM_FEED_MIMETYPE);
        assertEquals("Could not get feed", 200, getMethod.getStatusCode());
    }

    @Test
    public void testPartyRecordContent() throws Exception {

        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-party.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        XPath xpath = XPathHelper.getXPath();
        InputStream responseBodyAsStream = postMethod.getResponseBodyAsStream();
        Document docFromStream = XPathHelper.getDocFromStream(responseBodyAsStream);
        Document docFromFile = XPathHelper.getDocFromFile(fileName);

        String id = xpath.evaluate(Constants.RECORD_ID_PATH, docFromStream);
        assertNotNull("Entry missing id", id);
        assertTrue("Entry's id does not contain path to entry", id.contains(Constants.PATH_FOR_PARTIES));

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

        String authorName = xpath.evaluate(Constants.RECORD_AUTHOR_NAME_PATH, docFromStream);
        assertNotNull("Entry missing author name", authorName);
        String originalAuthorName = xpath.evaluate(Constants.RECORD_AUTHOR_NAME_PATH, docFromFile);
        assertNotNull("Original Entry missing author name", originalAuthorName);
        assertEquals("Entry's author name is incorrect", originalAuthorName, authorName);

        String draft = xpath.evaluate(Constants.RECORD_DRAFT_PATH, docFromStream);
        assertNotNull("Entry missing draft element", draft);
        assertEquals("Entry's should be draft", "yes", draft);


        //publish entry
        fileName = "/files/put/published-party.xml";
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
    }
}
