package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.model.Party;
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
    @SuppressWarnings("unchecked")
    public Party getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);

    }

    @Override
    @SuppressWarnings("unchecked")
    public Party getByKey(String uriKey) {
        Long id = DaoHelper.fromOtherBaseToDecimal(31, uriKey).longValue();
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
    public int softDelete(String uriKey) {
        Long id = DaoHelper.fromOtherBaseToDecimal(31, uriKey).longValue();
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Party o SET o.isActive = :isActive WHERE o.id = :id");
        query.setParameter("id", id);
        query.setParameter("isActive", false);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Party> getAllActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.isActive = true ORDER BY o.updated").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Party> getAllInActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.isActive = false ORDER BY o.updated").getResultList();
    }

    @Override
    public Party getLatestParty() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Party o WHERE o.updated = (SELECT MAX(o.updated) FROM Party o)");
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Party) resultList.get(0);
    }
}
