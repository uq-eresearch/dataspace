package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:07:08 AM
 */
public class RegistryApplication {
    private static RegistryConfiguration context;

    public static RegistryConfiguration getApplicationContext() {
        return context;
    }

    public void setApplicationContext(RegistryConfiguration context) {
        RegistryApplication.context = context;
    }
}
