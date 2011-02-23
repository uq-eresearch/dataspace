package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.DescriptionDao;
import net.metadata.dataspace.data.model.context.Description;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 23/02/2011
 * Time: 11:03:10 AM
 */
public class DescriptionDaoImpl extends JpaDao<Description> implements DescriptionDao, Serializable {

    public DescriptionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Description getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Description o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Description) resultList.get(0);
    }

    @Override
    public Description getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Description o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Description) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Description o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    public List<Description> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Description o WHERE o.isActive = true");
        return query.getResultList();
    }

    @Override
    public List<Description> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Description o WHERE o.isActive = false");
        return query.getResultList();
    }

    @Override
    public List<Description> getAllPublished() {
        return null;
    }

    @Override
    public List<Description> getAllUnpublished() {
        return null;
    }

    @Override
    public List<Description> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Description getMostRecentUpdated() {
        //TODO if a updated date property is added to Description table then this should be changed to get most recent updated Sourcel
        return getMostRecentInserted();
    }

    @Override
    public Description getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Description o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Description o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Description) resultList.get(0);
    }
}
