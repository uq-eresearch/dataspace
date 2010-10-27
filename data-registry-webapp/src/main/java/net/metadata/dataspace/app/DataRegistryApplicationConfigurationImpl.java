package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.DaoManager;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class DataRegistryApplicationConfigurationImpl implements DataRegistryApplicationConfiguration {

    private String registryTitle;
    private String version;
    private String uriPrefix;
    private DaoManager daoRegister;

    public DataRegistryApplicationConfigurationImpl() {
    }

    public DataRegistryApplicationConfigurationImpl(String version) {
        this.version = version;
    }


    public void setRegistryTitle(String registryTitle) {
        this.registryTitle = registryTitle;
    }

    public String getRegistryTitle() {
        return registryTitle;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @Override
    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setDaoRegister(DaoManager daoRegister) {
        this.daoRegister = daoRegister;
    }

    public DaoManager getDaoRegister() {
        return daoRegister;
    }
}
