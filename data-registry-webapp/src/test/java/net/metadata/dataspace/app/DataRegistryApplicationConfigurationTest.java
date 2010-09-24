package net.metadata.dataspace.app;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 11:26:36 AM
 */
public class DataRegistryApplicationConfigurationTest {

    @Test
    public void testGetVersion() throws Exception {

        //Get the expected value through application context
        DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();
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
            resourceAsStream = DataRegistryApplicationConfigurationImpl.class.getResourceAsStream("/revision.properties");
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
        String actualRevision = getProperty(properties, "data.registry.revision", "null");

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