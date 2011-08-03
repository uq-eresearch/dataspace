package net.metadata.dataspace.oaipmh;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 04/02/2011
 * Time: 3:30:36 PM
 */
public class OAIProperties extends Properties {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3903613774748461982L;
	private Logger logger = Logger.getLogger(getClass());

    public OAIProperties(String propertiesFile) {
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = OAIProperties.class.getResourceAsStream(propertiesFile);
            if (resourceAsStream == null) {
                logger.fatal("Configuration file not found, please ensure there is a " + propertiesFile + " on the classpath");
            }
            load(resourceAsStream);
        } catch (IOException ex) {
            logger.fatal("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }

    }
}
