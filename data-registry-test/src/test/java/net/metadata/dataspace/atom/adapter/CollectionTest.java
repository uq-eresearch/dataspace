package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:44 PM
 */
public class CollectionTest extends DataCollectionsRegistryTestCase {

    public void testPostCollection() throws Exception {
        String fileName = "/files.post/new-collection.xml";
        int status = TestHelper.postEntry(fileName, Constants.PATH_FOR_COLLECTIONS);
        assertTrue("Could not post entry, The server returned: " + status, status == 201);
    }
}
