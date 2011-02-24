package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:23:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class AgentDaoImplTest {

    @Autowired
    private AgentDao agentDao;
    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private JpaConnector jpaConnector;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        //Remove all
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingAgent() throws Exception {
        Agent agent = (Agent) entityCreator.getNextRecord(Agent.class);
        agent.setUpdated(new Date());
        entityManager.getTransaction().begin();
        int originalTableSize = agentDao.getAll().size();
        AgentVersion agentVersion = PopulatorUtil.getAgentVersion(agent);
        Subject subject1 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject1);
        Subject subject2 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject2);
        agent.getVersions().add(agentVersion);
        Source source = PopulatorUtil.getSource();
        agent.getCreators().add(agent);
        agent.setLocatedOn(source);
        agent.setSource(source);
        agent.setPublisher(agent);

        entityManager.persist(source);
        entityManager.persist(subject1);
        entityManager.persist(subject2);
        entityManager.persist(agentVersion);
        entityManager.persist(agent);
        entityManager.getTransaction().commit();

        Long id = agent.getId();
        Agent agentById = agentDao.getById(id);
        assertTrue("Table has " + agentDao.getAll().size() + " records", agentDao.getAll().size() == (originalTableSize + 1));
        assertEquals("Added and Retrieved records are not the same.", id, agentById.getId());
        assertEquals("Number of versions", 1, agentById.getVersions().size());
        assertEquals("Number of subjects", 2, agentById.getVersions().first().getSubjects().size());
    }


    @Test
    public void testEditingAgent() throws Exception {
        testAddingAgent();
        assertTrue("Table is empty", agentDao.getAll().size() != 0);
        List<Agent> agentList = agentDao.getAll();
        Agent agent = agentList.get(0);
        entityManager.getTransaction().begin();
        Long id = agent.getId();
        Date now = new Date();
        String content = "Updated Content";
        agent.getVersions().first().setDescription(content);
        agent.setUpdated(now);
        entityManager.merge(agent);
        entityManager.getTransaction().commit();
        Agent agentById = agentDao.getById(id);
        assertEquals("Modified and Retrieved records are not the same", agent, agentById);
        assertEquals("Update Date was not updated", now, agentById.getUpdated());
        assertEquals("content was not updated", content, agentById.getVersions().first().getDescription());
    }

    @Test
    public void testRemovingAgent() throws Exception {
        testAddingAgent();
        assertTrue("Table is empty", agentDao.getAll().size() != 0);
        List<Agent> agentList = agentDao.getAll();
        for (Agent agent : agentList) {
            agentDao.delete(agent);
        }
        assertTrue("Table is not empty", agentDao.getAll().size() == 0);
    }

    @Test
    public void testSoftDeleteAgent() throws Exception {
        testAddingAgent();
        assertTrue("Table is empty", agentDao.getAll().size() != 0);
        List<Agent> agentList = agentDao.getAll();
        int updated = 0;
        for (Agent agent : agentList) {
            updated = agentDao.softDelete(agent.getUriKey());
            agentDao.refresh(agent);
        }
        assertEquals("Updated rows", 1, updated);
        assertTrue("Table is empty", agentDao.getAll().size() != 0);
        assertEquals("Table has active records", 0, agentDao.getAllActive().size());
    }
}
