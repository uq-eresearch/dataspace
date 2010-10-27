package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.access.impl.*;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:39:29 AM
 */
public class DaoManager {

    private CollectionDaoImpl collectionDao;
    private PartyDaoImpl partyDao;
    private ServiceDaoImpl serviceDao;
    private ActivityDaoImpl activityDao;
    private SubjectDaoImpl subjectDao;

    public void setCollectionDao(CollectionDaoImpl collectionDao) {
        this.collectionDao = collectionDao;
    }

    public CollectionDaoImpl getCollectionDao() {
        return collectionDao;
    }

    public void setPartyDao(PartyDaoImpl partyDao) {
        this.partyDao = partyDao;
    }

    public PartyDaoImpl getPartyDao() {
        return partyDao;
    }

    public void setServiceDao(ServiceDaoImpl serviceDao) {
        this.serviceDao = serviceDao;
    }

    public ServiceDaoImpl getServiceDao() {
        return serviceDao;
    }

    public void setActivityDao(ActivityDaoImpl activityDao) {
        this.activityDao = activityDao;
    }

    public ActivityDaoImpl getActivityDao() {
        return activityDao;
    }

    public void setSubjectDao(SubjectDaoImpl subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectDaoImpl getSubjectDao() {
        return subjectDao;
    }

}
