package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:22:12 PM
 */
public class AgentDaoImpl extends AbstractRegistryDao<Agent> implements AgentDao, Serializable {

    public AgentDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public AgentVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM AgentVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
        query.setParameter("atomicNumber", atomicNumber);
        query.setParameter("parentAtomicNumber", parentAtomicNumber);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (AgentVersion) resultList.get(0);
    }

    @Override
    public Agent getByEmail(String email) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT DISTINCT(v.parent) FROM AgentVersion v WHERE :email IN elements(v.mboxes)");
        query.setParameter("email", email);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Agent) resultList.get(0);
    }

    @Override
    public Agent getByOriginalId(String originalId) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Agent o WHERE o.originalId = :originalId");
        query.setParameter("originalId", originalId);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Agent) resultList.get(0);
    }

}
