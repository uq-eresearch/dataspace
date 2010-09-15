package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 10:57:26 AM
 */
public class DataRegistryApplication {

    private DataRegistryApplicationContext configuration;


    public void setConfiguration(DataRegistryApplicationContext configuration) {
        this.configuration = configuration;
    }

    public DataRegistryApplicationContext getConfiguration() {
        return configuration;
    }
}
