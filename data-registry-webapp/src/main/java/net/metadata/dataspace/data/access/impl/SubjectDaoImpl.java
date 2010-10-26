package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.Subject;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
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
    @SuppressWarnings("unchecked")
    public Subject getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.id = :id").setParameter("id", id).getResultList();
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
    public List<Subject> getAllActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.isActive = true").getResultList();
    }

    @Override
    public List<Subject> getAllInActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.isActive = false").getResultList();
    }

    @Override
    public Subject getMostRecentInsertedCollection() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Subject o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }

}
