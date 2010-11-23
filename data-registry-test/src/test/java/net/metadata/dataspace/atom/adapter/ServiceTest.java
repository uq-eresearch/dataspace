package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
public class ServiceTest extends DataCollectionsRegistryTestCase {

    public void testServiceCRUD() throws Exception {
        AbderaClient client = TestHelper.login(Constants.USERNAME, Constants.PASSWORD);
        //Post Entry
        String fileName = "/files/post/new-service.xml";
        Response response = TestHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, response.getStatus());
        String newEntryLocation = response.getLocation().toString();
        response = TestHelper.getEntry(client, newEntryLocation);
        assertEquals("Could not get entry after post", 200, response.getStatus());
        //Edit Entry
        fileName = "/files/put/update-service.xml";
        response = TestHelper.putEntry(client, fileName, newEntryLocation);
        assertEquals("Could not edit entry", 200, response.getStatus());
        response = TestHelper.getEntry(client, newEntryLocation + "/2");
        assertEquals("Could not get second version of entry after edit", 200, response.getStatus());
        //Delete Entry
        response = TestHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, response.getStatus());
        response = TestHelper.getEntry(client, newEntryLocation);
        assertEquals("Entry should be GONE", 410, response.getStatus());
    }
}

