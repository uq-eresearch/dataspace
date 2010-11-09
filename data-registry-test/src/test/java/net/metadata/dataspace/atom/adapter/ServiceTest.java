package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.ClientResponse;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
public class ServiceTest extends DataCollectionsRegistryTestCase {

    public void testPostService() throws Exception {
        String fileName = "/files/post/new-service.xml";
        Response response = TestHelper.postEntry(fileName, Constants.PATH_FOR_SERVICES);
        int status = response.getStatus();
        assertEquals("Could not post entry" + status, status, 201);
        response = TestHelper.getEntry(Constants.URL_PREFIX + Constants.PATH_FOR_SERVICES + "/1");
        assertEquals("Could not get entry", response.getStatus(), 200);
    }

    public void testPutService() throws Exception {
        String fileName = "/files/put/update-service.xml";
        Response response = TestHelper.putEntry(fileName, Constants.PATH_FOR_SERVICES + "/1");
        int status = response.getStatus();
        assertEquals("Could not edit entry", status, 200);
        response = TestHelper.getEntry(Constants.URL_PREFIX + Constants.PATH_FOR_SERVICES + "/1/2");
        assertEquals("Could not get entry", response.getStatus(), 200);
    }

    public void testDeleteService() throws Exception {
        ClientResponse response = TestHelper.deleteEntry(Constants.URL_PREFIX + Constants.PATH_FOR_SERVICES + "/1");
        assertEquals("Could not delete entry", response.getStatus(), 200);

        response = TestHelper.getEntry(Constants.URL_PREFIX + Constants.PATH_FOR_SERVICES + "/1");
        assertEquals("Entry should be GONE", response.getStatus(), 410);
    }
}

