package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.connector.JpaConnector;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:39:29 AM
 */
public interface DaoManager {

    CollectionDao getCollectionDao();

    AgentDao getAgentDao();

    AgentVersionDao getAgentVersionDao();

    ServiceDao getServiceDao();

    ActivityDao getActivityDao();

    SubjectDao getSubjectDao();

    SourceDao getSourceDao();

    PublicationDao getPublicationDao();

    DescriptionDao getDescriptionDao();

    UserDao getUserDao();

    CollectionVersionDao getCollectionVersionDao();

    ServiceVersionDao getServiceVersionDao();

    ActivityVersionDao getActivityVersionDao();

    JpaConnector getJpaConnnector();
}
