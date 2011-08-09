package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:21:38 AM
 */
public class CollectionDaoImpl extends AbstractRegistryDao<Collection> implements CollectionDao, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6149363550719068910L;

	public CollectionDaoImpl() {}
	
	public CollectionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Version getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = getEntityManager().createQuery("SELECT o FROM CollectionVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Version) resultList.get(0);
    }

    @Override
    public Collection getByOriginalId(String originalId) {
        Query query = getEntityManager().createQuery("SELECT o FROM Collection o WHERE o.originalId = :originalId AND o.isActive = true");
        query.setParameter("originalId", originalId);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Collection) resultList.get(0);
    }
}
