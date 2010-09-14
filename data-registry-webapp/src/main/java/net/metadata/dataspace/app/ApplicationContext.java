package net.metadata.dataspace.app;

import net.metadata.dataspace.exception.InitializationException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class ApplicationContext implements ApplicationConfiguration, ServletContextListener {
    private final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

    private final String version;

    public ApplicationContext() throws InitializationException {
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = ApplicationContext.class.getResourceAsStream("/registry.properties");
            if (resourceAsStream == null) {
                throw new InitializationException("Configuration file not found, please ensure " +
                        "there is a 'ehmp.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            throw new InitializationException("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
        // try to load additional developer-specific settings
        File devPropertiesFile = new File("local/registry.properties");
        if (devPropertiesFile.isFile()) {
            InputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(devPropertiesFile);
                properties.load(fileInputStream);
            } catch (FileNotFoundException ex) {
                // should never happen since we checked before
            } catch (IOException ex) {
                throw new InitializationException("Failed to load development configuration properties", ex);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException ex) {
                        // so what?
                    }
                }
            }

        }
        this.version = getProperty(properties, "version", "beta") + ".";
    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Initialized Data Registry application.........OK");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Closed Data Registry application.........OK");
    }

    @Override
    public String getVersion() {
        return version;
    }

    private static String getProperty(Properties properties, String propertyName, String defaultValue) {
        String result = properties.getProperty(propertyName);
        if (result == null) {
            result = defaultValue;

            Logger.getLogger(ApplicationContext.class.getName()).log(Level.INFO,
                    String.format("No value for property '%s' found, falling back to default of '%s'", propertyName, defaultValue));
        }
        return result;
    }
}
