package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.Entry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: alabri
 * Date: 28/10/2010
 * Time: 2:44:36 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional()
public class AdapterOutputHelperTest {

    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private DaoManager daoManager;

    @Before
    public void setUp() throws Exception {
        JpaConnector jpaConnnector = daoManager.getJpaConnnector();
        EntityManager entityManager = jpaConnnector.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        User currentUser = new User("test", "Test User", "test@uq.edu.au");
        Agent agent = (Agent) entityCreator.getNextRecord(Agent.class);
        AgentVersion agentVersion = PopulatorUtil.getAgentVersion(agent);
        Collection collection = (Collection) entityCreator.getNextRecord(Collection.class);
        CollectionVersion collectionVersion = PopulatorUtil.getCollectionVersion(collection);
        Service service = (Service) entityCreator.getNextRecord(Service.class);
        ServiceVersion serviceVersion = PopulatorUtil.getServiceVersion(service);
        Activity activity = (Activity) entityCreator.getNextRecord(Activity.class);
        ActivityVersion activityVersion = PopulatorUtil.getActivityVersion(activity);

        agent.setUpdated(new Date());
        Subject subject1 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject1);
        Subject subject2 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject2);
        agent.getVersions().add(agentVersion);
        Source source = PopulatorUtil.getSource();
        agent.setSource(source);
        agent.setDescriptionAuthor(currentUser);

        collection.setUpdated(new Date());
        Subject subject3 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject3);
        Subject subject4 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject4);
        collection.getVersions().add(collectionVersion);
        collection.getCreators().add(agent);
        collection.setDescriptionAuthor(currentUser);
        agent.getMade().add(collection);
        collection.setSource(source);

        service.setUpdated(new Date());
        service.getVersions().add(serviceVersion);
        collectionVersion.getAccessedVia().add(service);
        serviceVersion.getSupportedBy().add(collection);
        service.setSource(source);

        activity.setUpdated(new Date());
        Subject subject5 = PopulatorUtil.getSubject();
        activityVersion.getSubjects().add(subject5);
        Subject subject6 = PopulatorUtil.getSubject();
        activityVersion.getSubjects().add(subject6);
        activity.getVersions().add(activityVersion);
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(agent);
        activity.setDescriptionAuthor(currentUser);
        agent.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        activity.setSource(source);

        entityManager.persist(source);
        entityManager.persist(subject1);
        entityManager.persist(subject2);
        entityManager.persist(subject3);
        entityManager.persist(subject4);
        entityManager.persist(subject5);
        entityManager.persist(subject6);
        entityManager.persist(agent);
        entityManager.persist(agentVersion);
        entityManager.persist(collection);
        entityManager.persist(collectionVersion);
        entityManager.persist(service);
        entityManager.persist(serviceVersion);
        entityManager.persist(activity);
        entityManager.persist(activityVersion);
        transaction.commit();
    }

//    @After
//    public void tearDown() throws Exception {
//        PopulatorUtil.cleanup();
//    }

    @Test
    public void testGetEntryFromAgent() throws Exception {
        List<Agent> agents = daoManager.getAgentDao().getAll();
        Agent agent = agents.get(agents.size() - 1);
        AgentVersion agentVersion = agent.getVersions().first();
        Entry entry = AdapterOutputHelper.getEntryFromEntity(agentVersion, true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey());
        assertEquals("Entry title", entry.getTitle(), agent.getTitle());
        assertEquals("Entry content", entry.getContent(), agent.getContent());
        assertEquals("Entry Description authors", entry.getSource().getAuthors().size(), 1);
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_MADE).size() >= 1);
    }

    @Test
    public void testGetEnteryFromCollection() throws Exception {
        List<Collection> collections = daoManager.getCollectionDao().getAll();
        Collection collection = collections.get(collections.size() - 1);
        CollectionVersion version = collection.getVersions().first();
        Entry entry = AdapterOutputHelper.getEntryFromEntity(version, true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        assertEquals("Entry title", entry.getTitle(), collection.getTitle());
        assertEquals("Entry content", entry.getContent(), collection.getContent());
        assertEquals("Entry Description authors", entry.getSource().getAuthors().size(), 1);
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_PAGE).size() >= 1);
        assertTrue("Entry should have at least one author", entry.getAuthors().size() >= 1);
        assertTrue("Entry should have at least one service", entry.getLinks(Constants.REL_IS_ACCESSED_VIA).size() >= 1);
        assertTrue("Entry should have at least one activity", entry.getLinks(Constants.REL_IS_OUTPUT_OF).size() >= 1);
    }

    @Test
    public void testGetEnteryFromActivity() throws Exception {
        List<Activity> activities = daoManager.getActivityDao().getAll();
        Activity activity = activities.get(activities.size() - 1);
        ActivityVersion version = activity.getVersions().first();
        Entry entry = AdapterOutputHelper.getEntryFromEntity(version, true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
        assertEquals("Entry title", entry.getTitle(), activity.getTitle());
        assertEquals("Entry content", entry.getContent(), activity.getContent());
        assertEquals("Entry Description authors", entry.getSource().getAuthors().size(), 1);
        assertTrue("Entry should have at least 1 category", entry.getCategories().size() >= 1);
        assertTrue("Entry should have at least one agent", entry.getLinks(Constants.REL_HAS_PARTICIPANT).size() >= 1);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_HAS_OUTPUT).size() >= 1);
    }

    @Test
    public void testGetEnteryFromService() throws Exception {
        List<Service> services = daoManager.getServiceDao().getAll();
        Service service = services.get(services.size() - 1);
        Entry entry = AdapterOutputHelper.getEntryFromEntity(service.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey());
        assertEquals("Entry title", entry.getTitle(), service.getTitle());
        assertEquals("Entry content", entry.getContent(), service.getContent());
        assertEquals("Entry Description authors", entry.getSource().getAuthors().size(), 1);
        assertTrue("Entry should have at least 1 category", entry.getCategories().size() >= 1);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_PAGE).size() >= 1);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_IS_SUPPORTED_BY).size() >= 1);
    }

    @Test
    public void testUpdateAgentFromEntry() throws Exception {
        List<Agent> agents = daoManager.getAgentDao().getAll();
        Agent agent = agents.get(agents.size() - 1);
        Entry entry = AdapterOutputHelper.getEntryFromEntity(agent.getVersions().first(), true);
        Version version = AdapterInputHelper.assembleAndValidateVersionFromEntry(agent, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", agent.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", agent.getVersions().first().getDescription(), version.getDescription());
    }

//    @Test
//    public void testUpdateCollectionFromEntry() throws Exception {
//        List<Collection> collections = daoManager.getCollectionDao().getAll();
//        Collection collection = collections.get(collections.size() - 1);
//        CollectionVersion firstVersion = collection.getVersions().first();
//        Entry entry = AdapterOutputHelper.getEntryFromEntity(firstVersion, true);
//        CollectionVersion version = (CollectionVersion) AdapterInputHelper.assembleAndValidateVersionFromEntry(collection, entry);
//        AdapterInputHelper.addRelations(entry, firstVersion);
//        assertNotNull("Could not update entry", version);
//        assertEquals("Entry title", firstVersion.getTitle(), version.getTitle());
//        assertEquals("Entry content", firstVersion.getDescription(), version.getDescription());
//    }

    @Test
    public void testUpdateServiceFromEntry() throws Exception {
        List<Service> services = daoManager.getServiceDao().getAll();
        Service service = services.get(services.size() - 1);
        Entry entry = AdapterOutputHelper.getEntryFromEntity(service.getVersions().first(), true);
        ServiceVersion version = (ServiceVersion) AdapterInputHelper.assembleAndValidateVersionFromEntry(service, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", service.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", service.getVersions().first().getDescription(), version.getDescription());
    }

    @Test
    public void testUpdateActivityFromEntry() throws Exception {
        List<Activity> activities = daoManager.getActivityDao().getAll();
        Activity activity = activities.get(activities.size() - 1);
        Entry entry = AdapterOutputHelper.getEntryFromEntity(activity.getVersions().first(), true);
        Version version = AdapterInputHelper.assembleAndValidateVersionFromEntry(activity, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", activity.getTitle(), version.getTitle());
        assertEquals("Entry content", activity.getContent(), version.getDescription());
    }
}
