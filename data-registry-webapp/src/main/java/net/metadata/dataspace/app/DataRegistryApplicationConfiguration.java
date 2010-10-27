package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.DaoRegister;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:34:49 PM
 */
public interface DataRegistryApplicationConfiguration {

    /**
     * Set the title of the registry application
     *
     * @param registryTitle
     */
    void setRegistryTitle(String registryTitle);

    /**
     * Get the title of the registry application
     *
     * @return
     */
    String getRegistryTitle();

    /**
     * Set the application version
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Retrieve the application version
     *
     * @return application version
     */
    String getVersion();

    /**
     * Retrieves the system wide uri prefix
     *
     * @return uri prefix as String
     */
    String getUriPrefix();

    /**
     * Return a register of dao
     *
     * @return DaoRegister bean
     */
    DaoRegister getDaoRegister();

}
