package net.metadata.dataspace.app;

import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.oaipmh.BasicOaiCatalog;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class RegistryConfigurationImpl implements RegistryConfiguration {

    private String registryTitle;
    private String version;
    private String uriPrefix;
    private DaoManager daoManager;
    private EntityCreator entityCreator;
    private AuthenticationManager authenticationManager;
    private AuthorizationManager authorizationManager;
    private BasicOaiCatalog oaiCatalog;

    public RegistryConfigurationImpl() {
    }

    public RegistryConfigurationImpl(String version) {
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

    public void setDaoManager(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }

    public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public void setOaiCatalog(BasicOaiCatalog oaiCatalog) {
        this.oaiCatalog = oaiCatalog;
    }

    public BasicOaiCatalog getOaiCatalog() {
        return oaiCatalog;
    }
}
