package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;

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
     * Provide an implementation of collection dao
     *
     * @return a collection dao
     */
    CollectionDao getCollectionDao();

    /**
     * Provide an implementation of the subject dao
     *
     * @return
     */
    SubjectDao getSubjectDao();


    PartyDao getPartyDao();
}
