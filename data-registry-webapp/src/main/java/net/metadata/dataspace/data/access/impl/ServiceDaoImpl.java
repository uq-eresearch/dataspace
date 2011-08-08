package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:02:48 AM
 */
public class ServiceDaoImpl extends AbstractRegistryDao<Service> implements ServiceDao, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6692375047398811759L;

	public ServiceDaoImpl() {}
	
	public ServiceDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public ServiceVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = getEntityManager().createQuery("SELECT o FROM ServiceVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (ServiceVersion) resultList.get(0);
    }

    @Override
    public Service getByOriginalId(String originalId) {
        Query query = getEntityManager().createQuery("SELECT o FROM Service o WHERE o.originalId = :originalId AND o.isActive = true");
        query.setParameter("originalId", originalId);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Service) resultList.get(0);
    }
}
