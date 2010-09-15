package net.metadata.dataspace.app;

import net.metadata.dataspace.exception.InitializationException;

import java.util.logging.Logger;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class ApplicationContext implements ApplicationConfiguration {
    private final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

    private final String version;

    public ApplicationContext(String version) throws InitializationException {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void init() {

    }
}
