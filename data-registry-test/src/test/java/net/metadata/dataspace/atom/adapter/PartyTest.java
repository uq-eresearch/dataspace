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

    public void testPartyCRUD() throws Exception {
        //Post Entry
        String fileName = "/files/post/new-party.xml";
        Response response = TestHelper.postEntry(fileName, Constants.PATH_FOR_PARTIES);
        assertEquals("Could not post entry", 201, response.getStatus());
        String newEntryLocation = response.getLocation().toString();
        response = TestHelper.getEntry(newEntryLocation);
        assertEquals("Could not get entry after post", 200, response.getStatus());
        //Edit entry
        fileName = "/files/put/update-party.xml";
        response = TestHelper.putEntry(fileName, newEntryLocation);
        assertEquals("Could not edit entry", 200, response.getStatus());
        response = TestHelper.getEntry(newEntryLocation + "/2");
        assertEquals("Could not get second version of entry after edit", 200, response.getStatus());
        //Delete entry
        response = TestHelper.deleteEntry(newEntryLocation);
        assertEquals("Could not delete entry", 200, response.getStatus());
        response = TestHelper.getEntry(newEntryLocation);
        assertEquals("Entry should be GONE", 410, response.getStatus());
    }
}