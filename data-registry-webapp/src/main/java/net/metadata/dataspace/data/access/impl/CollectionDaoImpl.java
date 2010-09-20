package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.model.Collection;

import java.io.Serializable;
import java.net.URI;
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
    public Collection getByKey(URI uri) {
        String uriValue = uri.toString();
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.uri = :uri").setParameter("uri", uriValue).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "uri should be unique";
        return (Collection) resultList.get(0);
    }
}
