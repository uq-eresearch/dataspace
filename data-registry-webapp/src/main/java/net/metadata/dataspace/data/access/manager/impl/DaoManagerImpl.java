package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;

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

}
