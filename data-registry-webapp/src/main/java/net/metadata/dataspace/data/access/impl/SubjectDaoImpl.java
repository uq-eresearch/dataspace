package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.resource.Subject;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:55 AM
 */
public class SubjectDaoImpl extends JpaDao<Subject> implements SubjectDao, Serializable {

    public SubjectDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Subject getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);

    }

    @Override
    public Subject getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Subject o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Subject> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.isActive = true");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Subject> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.isActive = false");
        return query.getResultList();
    }

    @Override
    public List<Subject> getAllPublished() {
        return null;
    }

    @Override
    public List<Subject> getAllUnpublished() {
        return null;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Subject> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Subject getMostRecentUpdated() {
        //TODO if a updated date property is added to Subject table then this should be changed to get most recent updated Subject
        return getMostRecentInserted();
    }

    @Override
    public Subject getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Subject o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }

    @Override
    public Subject getSubject(String vocabulary, String value) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.vocabulary = :vocabulary AND o.value = :value");
        query.setParameter("vocabulary", vocabulary);
        query.setParameter("value", value);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }
}
