package net.metadata.dataspace.app;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 4:51:11 PM
 */
public class DataCollectionRegistryTest extends DataCollectionsRegistryTestCase {

    public void testFrontPage() throws Exception {
        assertTextPresent("UQ Data Collections Registry");
    }
}
