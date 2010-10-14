package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
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
    @SuppressWarnings("unchecked")
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
        Long id = DaoHelper.fromOtherBaseToDecimal(31, uriKey).longValue();
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }

    @Override
    public int softDelete(String uriKey) {
        Long id = DaoHelper.fromOtherBaseToDecimal(31, uriKey).longValue();
        entityManagerSource.getEntityManager().getTransaction().begin();
        Query query = entityManagerSource.getEntityManager().createQuery("UPDATE Collection o SET o.isActive = :isActive WHERE o.id = :id");
        query.setParameter("isActive", false);
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        entityManagerSource.getEntityManager().getTransaction().commit();
        return updated;
    }

    @Override
    public List<Collection> getAllActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = true ORDER BY o.updated").getResultList();
    }

    @Override
    public List<Collection> getAllInActive() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.isActive = false ORDER BY o.updated").getResultList();
    }
}
