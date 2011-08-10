package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.RegistryDao;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 14/04/11
 * Time: 10:49 AM
 */
@Transactional
public abstract class AbstractRegistryDao<T> extends JpaDao<T> implements RegistryDao<T> {
	
	public AbstractRegistryDao() {}
	
	public AbstractRegistryDao(EntityManagerSource entityManagerSource) {
    	super(entityManagerSource);
    }

    @Override
    public T getById(Long id) {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.id = :id");
        query.setParameter("id", id);
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

    @Override
    public T getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

    @Override
    @Transactional
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = getEntityManager().createQuery("UPDATE " + getEntityName() + " o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllActive() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllInactive() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllPublished() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.updated DESC");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getRecentPublished(int limit) {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.publishDate DESC");
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllUnpublished() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NULL ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllPublishedBetween(Date fromDate, Date untilDate) {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.published IS NOT NULL AND o.updated BETWEEN :fromDate and :untilDate ORDER BY o.updated");
        query.setParameter("fromDate", fromDate);
        query.setParameter("untilDate", untilDate);
        return query.getResultList();
    }

    @Override
    public T getMostRecentUpdated() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.updated = (SELECT MAX(o.updated) FROM " + getEntityName() + " o)");
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

    @Override
    public T getMostRecentInserted() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM " + getEntityName() + " o)");
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

}
