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
    private AgentDao agentDao;
    private ServiceDao serviceDao;
    private ActivityDao activityDao;
    private SubjectDao subjectDao;
    private JpaConnector jpaConnnector;
    private AgentVersionDao agentVersionDao;
    private CollectionVersionDao collectionVersionDao;
    private ServiceVersionDao serviceVersionDao;
    private ActivityVersionDao activityVersionDao;
    private UserDao userDao;
    private SourceDao sourceDao;
    private PublicationDao publicationDao;
    private DescriptionDao descriptionDao;

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

    public void setJpaConnnector(JpaConnector jpaConnnector) {
        this.jpaConnnector = jpaConnnector;
    }

    public JpaConnector getJpaConnnector() {
        return jpaConnnector;
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

    public void setDescriptionDao(DescriptionDao descriptionDao) {
        this.descriptionDao = descriptionDao;
    }

    public DescriptionDao getDescriptionDao() {
        return descriptionDao;
    }
}
