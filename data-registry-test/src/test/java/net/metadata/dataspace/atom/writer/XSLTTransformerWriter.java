package net.metadata.dataspace.atom.writer;

/**
 * Author: alabri
 * Date: 07/02/2011
 * Time: 3:55:21 PM
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class XSLTTransformerWriter {

//    @Test

    /* public void testValidActivity() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //publish Entry
        String fileName = "/files/put/published-activity.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_ACTIVITIES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.MIME_TYPE_RIFCS);
        assertEquals("Get entry should return OK", 200, getMethod.getStatusCode());

        Source source = new StreamSource(getMethod.getResponseBodyAsStream());

        assertTrue(newEntryLocation + " is not a valid RIF-CS xml", SchemaHelper.isValidRIFCS(source, newEntryLocation));
    }

//    @Test

    public void testValidCollection() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //publish Entry
        String fileName = "/files/put/published-collection.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_COLLECTIONS);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.MIME_TYPE_RIFCS);
        assertEquals("Get entry should return OK", 200, getMethod.getStatusCode());

        Source source = new StreamSource(getMethod.getResponseBodyAsStream());

        assertTrue(newEntryLocation + " is not a valid RIF-CS xml", SchemaHelper.isValidRIFCS(source, newEntryLocation));
    }

//    @Test

    public void testValidParty() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //publish Entry
        String fileName = "/files/put/published-agent.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_AGENTS);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.MIME_TYPE_RIFCS);
        assertEquals("Get entry should return OK", 200, getMethod.getStatusCode());

        Source source = new StreamSource(getMethod.getResponseBodyAsStream());

        assertTrue(newEntryLocation + " is not a valid RIF-CS xml", SchemaHelper.isValidRIFCS(source, newEntryLocation));
    }

//    @Test

    public void testValidService() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //publish Entry
        String fileName = "/files/put/published-service.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.MIME_TYPE_RIFCS);
        assertEquals("Get entry should return OK", 200, getMethod.getStatusCode());

        Source source = new StreamSource(getMethod.getResponseBodyAsStream());

        assertTrue(newEntryLocation + " is not a valid RIF-CS xml", SchemaHelper.isValidRIFCS(source, newEntryLocation));
    }*/
}
