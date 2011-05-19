package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:02:37 AM
 */
public class ActivityDaoImpl extends AbstractRegistryDao<Activity> implements ActivityDao, Serializable {

    public ActivityDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public ActivityVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM ActivityVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (ActivityVersion) resultList.get(0);
    }

    @Override
    public Activity getByOriginalId(String originalId) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.originalId = :originalId");
        query.setParameter("originalId", originalId);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Activity) resultList.get(0);
    }

}
