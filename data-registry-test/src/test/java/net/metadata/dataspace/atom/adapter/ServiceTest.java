package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.abdera.protocol.Response;

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
        assertTrue("Could not post entry, The server returned: " + status, status == 201);
    }

    public void testPutService() throws Exception {
        String fileName = "/files/put/update-service.xml";
        Response response = TestHelper.putEntry(fileName, Constants.PATH_FOR_SERVICES + "/1");
        int status = response.getStatus();
        assertTrue("Could not edit entry, The server returned: " + status, status == 200);
    }


}

