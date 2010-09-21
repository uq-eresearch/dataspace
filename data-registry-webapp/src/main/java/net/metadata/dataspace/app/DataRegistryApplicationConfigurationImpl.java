package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.impl.CollectionDaoImpl;
import net.metadata.dataspace.data.access.impl.PartyDaoImpl;
import net.metadata.dataspace.data.access.impl.SubjectDaoImpl;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class DataRegistryApplicationConfigurationImpl implements DataRegistryApplicationConfiguration {

    private String version;
    private CollectionDaoImpl collectionDao;
    private SubjectDaoImpl subjectDao;
    private PartyDaoImpl partyDao;

    public DataRegistryApplicationConfigurationImpl() {
    }

    public DataRegistryApplicationConfigurationImpl(String version) {
        this.version = version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setCollectionDao(CollectionDaoImpl collectionDao) {
        this.collectionDao = collectionDao;
    }

    public CollectionDaoImpl getCollectionDao() {
        return collectionDao;
    }

    public void setSubjectDao(SubjectDaoImpl subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectDaoImpl getSubjectDao() {
        return subjectDao;
    }

    public void setPartyDao(PartyDaoImpl partyDao) {
        this.partyDao = partyDao;
    }

    public PartyDaoImpl getPartyDao() {
        return partyDao;
    }
}
