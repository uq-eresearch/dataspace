package net.metadata.dataspace.data.access.manager.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:40:11 PM
 */
public class DaoManagerImpl implements DaoManager {

    private CollectionDao collectionDao;
    private AgentDao agentDao;
    private ServiceDao serviceDao;
    private ActivityDao activityDao;
    private SubjectDao subjectDao;
    private EntityManagerSource entityManagerSource;
    private AgentVersionDao agentVersionDao;
    private CollectionVersionDao collectionVersionDao;
    private ServiceVersionDao serviceVersionDao;
    private ActivityVersionDao activityVersionDao;
    private UserDao userDao;
    private SourceDao sourceDao;
    private PublicationDao publicationDao;
    private FullNameDao fullNameDao;

    public void setCollectionDao(CollectionDao collectionDao) {
        this.collectionDao = collectionDao;
    }

    public CollectionDao getCollectionDao() {
        return collectionDao;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public AgentDao getAgentDao() {
        return agentDao;
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

    public void setJpaConnnector(EntityManagerSource entityManagerSource) {
        this.entityManagerSource = entityManagerSource;
    }

    public EntityManagerSource getEntityManagerSource() {
        return entityManagerSource;
    }

    public void setAgentVersionDao(AgentVersionDao agentVersionDao) {
        this.agentVersionDao = agentVersionDao;
    }

    public AgentVersionDao getAgentVersionDao() {
        return agentVersionDao;
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

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setSourceDao(SourceDao sourceDao) {
        this.sourceDao = sourceDao;
    }

    public SourceDao getSourceDao() {
        return sourceDao;
    }

    public void setPublicationDao(PublicationDao publicationDao) {
        this.publicationDao = publicationDao;
    }

    public PublicationDao getPublicationDao() {
        return publicationDao;
    }

    public void setFullNameDao(FullNameDao fullNameDao) {
        this.fullNameDao = fullNameDao;
    }

    public FullNameDao getFullNameDao() {
        return fullNameDao;
    }
}
