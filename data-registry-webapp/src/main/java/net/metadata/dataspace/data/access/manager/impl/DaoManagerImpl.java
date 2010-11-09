package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.connector.JpaConnector;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:40:11 PM
 */
public class DaoManagerImpl implements DaoManager {

    private CollectionDao collectionDao;
    private PartyDao partyDao;
    private ServiceDao serviceDao;
    private ActivityDao activityDao;
    private SubjectDao subjectDao;
    private JpaConnector jpaConnnector;
    private PartyVersionDao partyVersionDao;
    private CollectionVersionDao collectionVersionDao;
    private ServiceVersionDao serviceVersionDao;
    private ActivityVersionDao activityVersionDao;

    public void setCollectionDao(CollectionDao collectionDao) {
        this.collectionDao = collectionDao;
    }

    public CollectionDao getCollectionDao() {
        return collectionDao;
    }

    public void setPartyDao(PartyDao partyDao) {
        this.partyDao = partyDao;
    }

    public PartyDao getPartyDao() {
        return partyDao;
    }

    public void setServiceDao(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    public ServiceDao getServiceDao() {
        return serviceDao;
    }

    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    public ActivityDao getActivityDao() {
        return activityDao;
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectDao getSubjectDao() {
        return subjectDao;
    }

    public void setJpaConnnector(JpaConnector jpaConnnector) {
        this.jpaConnnector = jpaConnnector;
    }

    public JpaConnector getJpaConnnector() {
        return jpaConnnector;
    }

    public void setPartyVersionDao(PartyVersionDao partyVersionDao) {
        this.partyVersionDao = partyVersionDao;
    }

    public PartyVersionDao getPartyVersionDao() {
        return partyVersionDao;
    }

    public void setCollectionVersionDao(CollectionVersionDao collectionVersionDao) {
        this.collectionVersionDao = collectionVersionDao;
    }

    public CollectionVersionDao getCollectionVersionDao() {
        return collectionVersionDao;
    }

    public void setServiceVersionDao(ServiceVersionDao serviceVersionDao) {
        this.serviceVersionDao = serviceVersionDao;
    }

    public ServiceVersionDao getServiceVersionDao() {
        return serviceVersionDao;
    }

    public void setActivityVersionDao(ActivityVersionDao activityVersionDao) {
        this.activityVersionDao = activityVersionDao;
    }

    public ActivityVersionDao getActivityVersionDao() {
        return activityVersionDao;
    }
}
