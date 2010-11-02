package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Service;
import net.metadata.dataspace.data.model.ServiceVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:02:48 AM
 */
public class ServiceDaoImpl extends JpaDao<Service> implements ServiceDao, Serializable {

    public ServiceDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Service getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Service) resultList.get(0);
    }

    @Override
    public Service getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Service) resultList.get(0);
    }

    @Override
    public ServiceVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM ServiceVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (ServiceVersion) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Service o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Service> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Service> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    public Service getMostRecentUpdated() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.updated = (SELECT MAX(o.updated) FROM Service o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Service) resultList.get(0);
    }

    @Override
    public Service getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Service o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Service o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Service) resultList.get(0);
    }

}
