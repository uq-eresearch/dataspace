package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
public class ServiceTest extends DataCollectionsRegistryTestCase {

    public void testPostService() throws Exception {
        String fileName = "/files.post/new-service.xml";
        int status = TestHelper.postEntry(fileName, Constants.PATH_FOR_SERVICES);
        assertTrue("Could not post entry, The server returned: " + status, status == 201);
    }
}

