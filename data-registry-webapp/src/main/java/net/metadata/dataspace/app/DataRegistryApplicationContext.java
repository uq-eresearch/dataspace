package net.metadata.dataspace.app;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class DataRegistryApplicationContext implements ApplicationConfiguration {

    private String version;

    public DataRegistryApplicationContext() {
    }

    public DataRegistryApplicationContext(String s) {
        this.version = s;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
