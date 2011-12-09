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
    	// It's possible the key is null, so check first
    	if (uriKey == null)
    		return null;
    	// Convert to atomic number and search
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
    public List<T> getAllActive() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true ORDER BY o.recordTimestamp");
        return getActive(0,0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getActive(int pageSize, int pageNumber) {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true ORDER BY o.recordTimestamp");
        applyPaging(query, pageSize, pageNumber);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllInactive() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = false ORDER BY o.recordTimestamp");
        return query.getResultList();
    }

    @Override
    public List<T> getAllPublished() {
    	return getPublished(0,0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getPublished(int pageSize, int pageNumber) {
    	Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.recordTimestamp DESC");
        applyPaging(query, pageSize, pageNumber);
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
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NULL ORDER BY o.recordTimestamp");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllPublishedBetween(Date fromDate, Date untilDate) {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.published IS NOT NULL AND o.recordTimestamp BETWEEN :fromDate and :untilDate ORDER BY o.recordTimestamp");
        query.setParameter("fromDate", fromDate);
        query.setParameter("untilDate", untilDate);
        return query.getResultList();
    }

    @Override
    public T getMostRecentUpdated() {
        Query query = getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.recordTimestamp = (SELECT MAX(o.recordTimestamp) FROM " + getEntityName() + " o)");
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

    protected Query applyPaging(Query query, int pageSize, int pageNumber) {
    	if (pageSize > 0) {
	    	query.setMaxResults(pageSize);
	    	pageNumber = Math.max(pageNumber, 1);
	    	query.setFirstResult((pageNumber-1)*pageSize);
    	}
    	return query;
    }

}
