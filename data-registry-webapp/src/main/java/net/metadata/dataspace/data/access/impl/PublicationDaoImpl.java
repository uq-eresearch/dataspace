package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PublicationDao;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:08 PM
 */
public class PublicationDaoImpl extends JpaDao<Publication> implements PublicationDao, Serializable {

    public PublicationDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Publication getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Publication o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Publication) resultList.get(0);
    }

    @Override
    public Publication getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Publication o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Publication) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Publication o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    public List<Publication> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Publication o WHERE o.isActive = true");
        return query.getResultList();
    }

    @Override
    public List<Publication> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Publication o WHERE o.isActive = false");
        return query.getResultList();
    }

    @Override
    public List<Publication> getAllPublished() {
        return null;
    }

    @Override
    public List<Publication> getAllUnpublished() {
        return null;
    }

    @Override
    public List<Publication> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Publication getMostRecentUpdated() {
        //TODO if a updated date property is added to Publication table then this should be changed to get most recent updated Publication
        return getMostRecentInserted();
    }

    @Override
    public Publication getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Publication o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Publication o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Publication) resultList.get(0);
    }
}
