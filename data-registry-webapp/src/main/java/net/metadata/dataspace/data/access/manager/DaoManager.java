package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.access.*;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:39:29 AM
 */
public interface DaoManager {

    CollectionDao getCollectionDao();

    PartyDao getPartyDao();

    ServiceDao getServiceDao();

    ActivityDao getActivityDao();

    SubjectDao getSubjectDao();
}
