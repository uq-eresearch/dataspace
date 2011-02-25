package net.metadata.dataspace.app;

import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.version.AgentVersion;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:07:08 AM
 */
public class RegistryApplication {
    private static RegistryConfiguration context;

    public static RegistryConfiguration getApplicationContext() {
        return context;
    }

    public void setApplicationContext(RegistryConfiguration context) {
        RegistryApplication.context = context;
//        createFirstAgent();
    }

    private void createFirstAgent() {
        AgentDao agentDao = getApplicationContext().getDaoManager().getAgentDao();
        if (agentDao.getByEmail("info@dataspace.uq.edu.au") == null) {
            EntityCreator entityCreator = getApplicationContext().getEntityCreator();
            EntityManager entityManager = getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            Date now = new Date();

            Source source = entityCreator.getNextSource();
            source.setTitle("The University of Queensland");
            source.setSourceURI(Constants.UQ_SOURCE_URI);
            source.setUpdated(now);


            Agent agent = ((Agent) entityCreator.getNextRecord(Agent.class));
            AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
            version.setTitle("The University of Queensland");
            version.setDescription("The University of Queensland (UQ) is one of Australia's premier learning and research institutions. It is the oldest university in Queensland and has produced almost 180,000 graduates since opening in 1911. Its graduates have gone on to become leaders in all areas of society and industry.");
            version.setAlternative("UQ");
            version.setUpdated(now);
            Set<String> authors = new HashSet<String>();
            authors.add("The University of Queensland DataSpace");
            version.setAuthors(authors);
            version.setType(AgentType.GROUP);
            version.setMbox("info@dataspace.uq.edu.au");
            version.setPage("http://uq.edu.au");
            transaction.begin();
            version.setParent(agent);
            version.getParent().setPublished(version);
            agent.getVersions().add(version);
            agent.setUpdated(now);
            agent.setLocatedOn(source);
            agent.setSource(source);
            agent.getCreators().add(agent);

            entityManager.persist(source);
            entityManager.persist(version);
            entityManager.persist(agent);
            transaction.commit();
        }
    }
}
