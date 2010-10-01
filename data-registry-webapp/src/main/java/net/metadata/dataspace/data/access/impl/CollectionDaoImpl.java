package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.util.DaoHelper;

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
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "uri should be unique";
        return (Collection) resultList.get(0);
    }

}
