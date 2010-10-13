package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.connector.JpaConnector;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:34:49 PM
 */
public interface DataRegistryApplicationConfiguration {

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
     * Provide an implementation of collection dao
     *
     * @return CollectionDao
     */
    CollectionDao getCollectionDao();

    /**
     * Provide an implementation of the subject dao
     *
     * @return SubjectDao
     */
    SubjectDao getSubjectDao();

    /**
     * Provides an implementation of party dao
     *
     * @return PartyDao
     */
    PartyDao getPartyDao();

    /**
     * Gets the JPA connector
     *
     * @return JpaConnector
     */
    JpaConnector getJpaConnector();

}
