package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.PartyVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:22:12 PM
 */
public class PartyDaoImpl extends JpaDao<Party> implements PartyDao, Serializable {

    public PartyDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Party getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);
    }

    @Override
    public Party getByKey(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);
    }

    @Override
    public PartyVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM PartyVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (PartyVersion) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Party o SET o.isActive = :isActive WHERE o.atomicNumber = :atomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Party> getAllActive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.isActive = true ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Party> getAllInactive() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.isActive = false ORDER BY o.updated");
        return query.getResultList();
    }

    @Override
    public Party getMostRecentUpdated() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.updated = (SELECT MAX(o.updated) FROM Party o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);
    }

    @Override
    public Party getMostRecentInserted() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.atomicNumber = (SELECT MAX(o.atomicNumber) FROM Party o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);
    }
}
