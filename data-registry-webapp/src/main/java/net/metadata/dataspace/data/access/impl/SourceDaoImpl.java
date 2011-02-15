package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:21 PM
 */
public class SourceDaoImpl extends JpaDao<Source> implements SourceDao, Serializable {

    public SourceDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Source getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Source o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Source) resultList.get(0);
    }

    @Override
    public Source getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Source o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Source) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Source o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    public List<Source> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Source o WHERE o.isActive = true");
        return query.getResultList();
    }

    @Override
    public List<Source> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Source o WHERE o.isActive = false");
        return query.getResultList();
    }

    @Override
    public List<Source> getAllPublished() {
        return null;
    }

    @Override
    public List<Source> getAllUnpublished() {
        return null;
    }

    @Override
    public List<Source> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Source getMostRecentUpdated() {
        //TODO if a updated date property is added to Source table then this should be changed to get most recent updated Sourcel
        return getMostRecentInserted();
    }

    @Override
    public Source getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Source o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Source o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Source) resultList.get(0);
    }
}
