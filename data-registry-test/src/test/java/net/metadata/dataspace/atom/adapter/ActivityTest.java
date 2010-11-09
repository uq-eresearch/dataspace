package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.abdera.protocol.Response;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 3:08:31 PM
 */
public class ActivityTest extends DataCollectionsRegistryTestCase {

    public void testPostActivity() throws Exception {
        String fileName = "/files/post/new-activity.xml";
        Response response = TestHelper.postEntry(fileName, Constants.PATH_FOR_ACTIVITIES);
        int status = response.getStatus();
        assertTrue("Could not post entry, The server returned: " + status, status == 201);
    }
}
