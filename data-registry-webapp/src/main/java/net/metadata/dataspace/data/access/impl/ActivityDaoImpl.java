package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.Activity;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:02:37 AM
 */
public class ActivityDaoImpl extends JpaDao<Activity> implements ActivityDao, Serializable {

    public ActivityDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Activity getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Activity) resultList.get(0);
    }

    @Override
    public Activity getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Activity) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Activity o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Activity> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Activity> getAllInActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    public Activity getMostRecentUpdated() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.updated = (SELECT MAX(o.updated) FROM Activity o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Activity) resultList.get(0);
    }

    @Override
    public Activity getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Activity o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Activity o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Activity) resultList.get(0);
    }

}