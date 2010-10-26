package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.impl.CollectionDaoImpl;
import net.metadata.dataspace.data.access.impl.PartyDaoImpl;
import net.metadata.dataspace.data.access.impl.SubjectDaoImpl;
import net.metadata.dataspace.data.connector.JpaConnector;

/**
 * User: alabri
 * Date: 13/09/2010
 * Time: 3:37:39 PM
 */
public class DataRegistryApplicationConfigurationImpl implements DataRegistryApplicationConfiguration {

    private String registryTitle;
    private String version;
    private CollectionDaoImpl collectionDao;
    private SubjectDaoImpl subjectDao;
    private PartyDaoImpl partyDao;
    private String uriPrefix;
    private JpaConnector jpaConnector;

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

    public void setCollectionDao(CollectionDaoImpl collectionDao) {
        this.collectionDao = collectionDao;
    }

    @Override
    public CollectionDaoImpl getCollectionDao() {
        return collectionDao;
    }

    public void setSubjectDao(SubjectDaoImpl subjectDao) {
        this.subjectDao = subjectDao;
    }

    @Override
    public SubjectDaoImpl getSubjectDao() {
        return subjectDao;
    }

    public void setPartyDao(PartyDaoImpl partyDao) {
        this.partyDao = partyDao;
    }

    @Override
    public PartyDaoImpl getPartyDao() {
        return partyDao;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @Override
    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setJpaConnector(JpaConnector jpaConnector) {
        this.jpaConnector = jpaConnector;
    }

    public JpaConnector getJpaConnector() {
        return jpaConnector;
    }
}
