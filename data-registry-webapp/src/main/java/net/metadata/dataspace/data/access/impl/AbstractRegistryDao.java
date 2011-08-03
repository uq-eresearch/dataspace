package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.RegistryDao;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Entity;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 14/04/11
 * Time: 10:49 AM
 */
public abstract class AbstractRegistryDao<T> extends JpaDao<T> implements RegistryDao<T> {

    public AbstractRegistryDao(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public T getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.id = :id");
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
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE " + getEntityName() + " o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllPublished() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.updated DESC");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getRecentPublished(int limit) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.publishDate DESC");
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllUnpublished() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.isActive = true AND o.published IS NULL ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllPublishedBetween(Date fromDate, Date untilDate) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.published IS NOT NULL AND o.updated BETWEEN :fromDate and :untilDate ORDER BY o.updated");
        query.setParameter("fromDate", fromDate);
        query.setParameter("untilDate", untilDate);
        return query.getResultList();
    }

    @Override
    public T getMostRecentUpdated() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.updated = (SELECT MAX(o.updated) FROM " + getEntityName() + " o)");
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }

    @Override
    public T getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM " + getEntityName() + " o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM " + getEntityName() + " o)");
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (T) resultList.get(0);
    }


    private String getEntityName() {
        Class<T> actualClassParameter = getActualClassParameter();
        Entity annotation = actualClassParameter.getAnnotation(Entity.class);
        String tableName = (annotation.name() != null) && (annotation.name().length() > 0) ? annotation.name() : actualClassParameter.getSimpleName();
        return tableName;
    }

    private Class<T> getActualClassParameter() {
        @SuppressWarnings("unchecked")
        Class<T> actualClassParameter = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return actualClassParameter;
    }
}
