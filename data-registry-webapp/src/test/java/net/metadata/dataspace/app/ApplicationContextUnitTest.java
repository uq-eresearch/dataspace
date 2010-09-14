package net.metadata.dataspace.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 11:26:36 AM
 */
public class ApplicationContextUnitTest {
    @Test
    public void testGetVersion() throws Exception {
        String version = DataRegistryApplication.getConfiguration().getVersion();

        assertEquals("0.1.0.0", version);
    }

}
