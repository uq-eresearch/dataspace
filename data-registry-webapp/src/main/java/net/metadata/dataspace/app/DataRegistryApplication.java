package net.metadata.dataspace.app;

import net.metadata.dataspace.exception.InitializationException;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 10:57:26 AM
 */
public class DataRegistryApplication {

    private static final ApplicationConfiguration configuration;

    static {
        try {
            configuration = new ApplicationContext();
        } catch (InitializationException ex) {
            throw new RuntimeException("Failed to initialize application", ex);
        }
    }

    public static ApplicationConfiguration getConfiguration() {
        return configuration;
    }
}
