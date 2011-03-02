package net.metadata.dataspace.app;

import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.oaipmh.OAIProperties;
import net.metadata.dataspace.oaipmh.RIFCSOaiCatalog;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:34:49 PM
 */
public interface RegistryConfiguration {

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
     * Get the email address of the registry application
     *
     * @return email address
     */
    String getRegistryEmail();

    /**
     * Set the email for the registry
     *
     * @param registryEmail
     */
    void setRegistryEmail(String registryEmail);

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
    DaoManager getDaoManager();

    /**
     * Return an entity creator
     *
     * @return EntityCreator
     */
    EntityCreator getEntityCreator();

    /**
     * Return the application's authentication manager
     *
     * @return AuthenticationManager
     */
    AuthenticationManager getAuthenticationManager();

    /**
     * Return the application's authorization manager
     *
     * @return AuthorizationManager
     */
    AuthorizationManager getAuthorizationManager();

    /**
     * Return OAI-PMH catalog
     *
     * @return
     */
    RIFCSOaiCatalog getOaiCatalog();

    OAIProperties getOaiProperties();

}
