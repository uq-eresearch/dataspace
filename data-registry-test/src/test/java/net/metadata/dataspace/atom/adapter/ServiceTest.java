package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
public class ServiceTest extends DataCollectionsRegistryTestCase {

    public void testServiceCRUD() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = TestHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-service.xml";
        PostMethod postMethod = TestHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        //Get entry
        GetMethod getMethod = TestHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get entry after post", 200, getMethod.getStatusCode());
        //get first version
        getMethod = TestHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get first version of entry after post", 200, getMethod.getStatusCode());
        //get working copy
        getMethod = TestHelper.getEntry(client, newEntryLocation + "/working-copy", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get working copy after post", 200, getMethod.getStatusCode());
        //Edit Entry
        fileName = "/files/put/update-service.xml";
        PutMethod putMethod = TestHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not edit entry", 200, putMethod.getStatusCode());
        //get second version
        getMethod = TestHelper.getEntry(client, newEntryLocation + "/2", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get second version of entry after edit", 200, getMethod.getStatusCode());
        //Get version history
        getMethod = TestHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get working copy after post", 200, getMethod.getStatusCode());
        //Delete Entry
        DeleteMethod deleteMethod = TestHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, deleteMethod.getStatusCode());
        //check that entry is deleted
        getMethod = TestHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Entry should be GONE", 410, getMethod.getStatusCode());
    }
}

