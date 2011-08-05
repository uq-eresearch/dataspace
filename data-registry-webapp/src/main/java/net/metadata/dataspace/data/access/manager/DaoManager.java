package net.metadata.dataspace.data.access.manager;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.*;

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

    FullNameDao getFullNameDao();

    UserDao getUserDao();

    CollectionVersionDao getCollectionVersionDao();

    ServiceVersionDao getServiceVersionDao();

    ActivityVersionDao getActivityVersionDao();

    EntityManagerSource getEntityManagerSource();
}
