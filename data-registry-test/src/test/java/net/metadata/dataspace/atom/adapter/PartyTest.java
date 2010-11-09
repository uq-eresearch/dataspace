package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataCollectionsRegistryTestCase;
import net.metadata.dataspace.atom.util.TestHelper;
import org.apache.abdera.protocol.Response;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:34 PM
 */
public class PartyTest extends DataCollectionsRegistryTestCase {

    public void testPostParty() throws Exception {
        String fileName = "/files/post/new-party.xml";
        Response response = TestHelper.postEntry(fileName, Constants.PATH_FOR_PARTIES);
        int status = response.getStatus();
        assertTrue("Could not post entry, The server returned: " + status, status == 201);
    }

}
