package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.context.Mbox;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:22:12 PM
 */
@Transactional
public class AgentDaoImpl extends AbstractRegistryDao<Agent> implements AgentDao, Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 6085537803929252268L;

	public AgentDaoImpl() {}

	public AgentDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

	@Override
	@Transactional
	public void delete(Agent agent) {
		dereferenceMboxes(agent);
		super.delete(agent);
	}

    @Override
    public AgentVersion getByVersion(String uriKey, String version) {
        int parentAtomicNumber = DaoHelper.fromOtherBaseToDecimal(31, uriKey);
        int atomicNumber = DaoHelper.fromOtherBaseToDecimal(31, version);
        Query query = getEntityManager().createQuery("SELECT o FROM AgentVersion o WHERE o.atomicNumber = :atomicNumber AND o.parent.atomicNumber = :parentAtomicNumber");
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
    public Agent getByOriginalId(String originalId) {
        Query query = getEntityManager().createQuery("SELECT o FROM Agent o WHERE o.originalId = :originalId");
        query.setParameter("originalId", originalId);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Agent) resultList.get(0);
    }

    private void dereferenceMboxes(Agent agent) {
		Query query = getEntityManager().createQuery("from Mbox m where m.owner = :agent");
		query.setParameter("agent", agent);
		@SuppressWarnings("unchecked")
		List<Mbox> mboxes = (List<Mbox>) query.getResultList();
		for (Mbox m : mboxes) {
			m.setOwner(null);
		}
    }

}
