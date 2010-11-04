package net.metadata.dataspace.app;

import net.sourceforge.jwebunit.junit.WebTestCase;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 4:47:17 PM
 */
public abstract class DataCollectionsRegistryTestCase extends WebTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        String baseUrl = System.getProperty("data.registry.uri.prefix", "http://localhost:9635");
        setBaseUrl(baseUrl);
        gotoPage("/");
    }
}
