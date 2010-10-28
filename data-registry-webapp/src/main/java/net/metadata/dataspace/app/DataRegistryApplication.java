package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:07:08 AM
 */
public class DataRegistryApplication {
    private static DataRegistryApplicationConfiguration applicationContext;

    public static DataRegistryApplicationConfiguration getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(DataRegistryApplicationConfiguration context) {
        DataRegistryApplication.applicationContext = context;
    }

}
