package net.metadata.dataspace.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 11:26:36 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class DataRegistryApplicationConfigurationTest {

    @Autowired
    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl;

    @Test
    public void testGetVersion() throws Exception {

        //Get the expected value through application context

        String expectedVersionNumber = dataRegistryApplicationConfigurationImpl.getVersion();

        //Get the actual values
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = DataRegistryApplicationConfigurationImpl.class.getResourceAsStream("/registry.properties");
            if (resourceAsStream == null) {
                throw new Exception("Configuration file not found, please ensure there is a 'registry.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            throw new Exception("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
        try {
            resourceAsStream = DataRegistryApplicationConfigurationImpl.class.getResourceAsStream("/META-INF/svninfo.properties");
            if (resourceAsStream == null) {
                throw new Exception("Configuration file not found, please ensure there is a 'revision.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            throw new Exception("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
        String actualVersion = getProperty(properties, "data.registry.version", "null");
        String actualRevision = getProperty(properties, "revision", "null");

        assertEquals(expectedVersionNumber, actualVersion + "." + actualRevision);
    }

    private static String getProperty(Properties properties, String propertyName, String defaultValue) {
        String result = properties.getProperty(propertyName);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
}
