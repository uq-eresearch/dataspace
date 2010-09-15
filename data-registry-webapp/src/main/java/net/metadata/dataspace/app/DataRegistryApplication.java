package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 10:57:26 AM
 */
public class DataRegistryApplication {

    private ApplicationContext configuration;


    public void setConfiguration(ApplicationContext configuration) {
        this.configuration = configuration;
    }

    public ApplicationContext getConfiguration() {
        return configuration;
    }
}
