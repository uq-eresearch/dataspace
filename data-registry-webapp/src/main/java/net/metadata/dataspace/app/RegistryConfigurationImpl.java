package net.metadata.dataspace.app;

import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.oaipmh.OAIProperties;
import net.metadata.dataspace.oaipmh.RIFCSOaiCatalog;
import org.apache.log4j.Logger;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class RegistryConfigurationImpl implements RegistryConfiguration {
    private Logger logger = Logger.getLogger(getClass());
    private String registryTitle;
    private String version;
    private String uriPrefix;
    private DaoManager daoManager;
    private EntityCreator entityCreator;
    private AuthenticationManager authenticationManager;
    private AuthorizationManager authorizationManager;
    private RIFCSOaiCatalog oaiCatalog;
    private OAIProperties oaiProperties;
    private String registryEmail;
    private String registryLicense;
    private String registryRights;

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

    public void setRegistryEmail(String registryEmail) {
        this.registryEmail = registryEmail;
    }

    public String getRegistryEmail() {
        return registryEmail;
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

    public void setOaiCatalog(RIFCSOaiCatalog oaiCatalog) {
        this.oaiCatalog = oaiCatalog;
    }

    public RIFCSOaiCatalog getOaiCatalog() {
        return oaiCatalog;
    }

    public void setOaiProperties(OAIProperties oaiProperties) {
        this.oaiProperties = oaiProperties;
    }

    public OAIProperties getOaiProperties() {
        return oaiProperties;
    }

    public void setRegistryLicense(String license) {
        this.registryLicense = license;
    }

    public String getRegistryLicense() {
        return registryLicense;
    }

    public void setRegistryRights(String registryRights) {
        this.registryRights = registryRights;
    }

    public String getRegistryRights() {
        return registryRights;
    }

    public void cleanUp() {
        getDaoManager().getJpaConnnector().getEntityManager().close();
        logger.info("Successfully closed the database connection.... OK!");
    }
}
