package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.sequencer.CollectionAtomicSequencer;
import net.metadata.dataspace.data.sequencer.PartyAtomicSequencer;
import net.metadata.dataspace.data.sequencer.SubjectAtomicSequencer;

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

    PartyAtomicSequencer getPartyAtomicSequencer();

    CollectionAtomicSequencer getCollectionAtomicSequencer();

    SubjectAtomicSequencer getSubjectAtomicSequencer();

}
