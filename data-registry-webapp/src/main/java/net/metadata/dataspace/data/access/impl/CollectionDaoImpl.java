package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:21:38 AM
 */
public class CollectionDaoImpl extends JpaDao<Collection> implements CollectionDao, Serializable {

    public CollectionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Collection getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);

    }

    @Override
    public Collection getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }

    @Override
    public CollectionVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM CollectionVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (CollectionVersion) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Collection o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("isActive", false);
        query.setParameter("atomicNumber", atomicNumber);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Collection> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Collection> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Collection> getAllPublished() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = true AND o.published IS NOT NULL ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Collection> getAllUnpublished() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = true AND o.published IS NULL ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Collection> getAllPublishedBetween(Date fromDate, Date untilDate) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.published IS NOT NULL AND o.updated BETWEEN :fromDate and :untilDate ORDER BY o.updated");
        query.setParameter("fromDate", fromDate);
        query.setParameter("untilDate", untilDate);
        return query.getResultList();
    }

    @Override
    public Collection getMostRecentUpdated() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.updated = (SELECT MAX(o.updated) FROM Collection o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }

    @Override
    public Collection getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Collection o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }
}
