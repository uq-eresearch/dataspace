package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.FullNameDao;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 14/04/11
 * Time: 10:39 AM
 */
public class FullNameDaoImpl extends JpaDao<FullName> implements FullNameDao, Serializable {

    public FullNameDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public FullName getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM FullName o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (FullName) resultList.get(0);
    }

    @Override
    public FullName getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM FullName o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (FullName) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE FullName o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    public List<FullName> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM FullName o WHERE o.isActive = true");
        return query.getResultList();
    }

    @Override
    public List<FullName> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM FullName o WHERE o.isActive = false");
        return query.getResultList();
    }

    @Override
    public List<FullName> getAllPublished() {
        return null;
    }

    @Override
    public List<FullName> getAllUnpublished() {
        return null;
    }

    @Override
    public List<FullName> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public FullName getMostRecentUpdated() {
        //TODO if a updated date property is added to FullName table then this should be changed to get most recent updated FullName
        return getMostRecentInserted();
    }

    @Override
    public FullName getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM FullName o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Publication o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (FullName) resultList.get(0);
    }
}