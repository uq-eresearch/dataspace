package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.Entry;
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
public class AdapterHelperTest {

    @Autowired
    private CollectionDao collectionDao;
    @Autowired
    private AgentDao agentDao;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private EntityCreator entityCreator;
    @Autowired
    private JpaConnector jpaConnector;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();
        entityManager.getTransaction().begin();
        Agent agent = (Agent) entityCreator.getNextRecord(Agent.class);
        agent.setUpdated(new Date());
        AgentVersion agentVersion = PopulatorUtil.getAgentVersion(agent);
        Subject subject1 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject1);
        Subject subject2 = PopulatorUtil.getSubject();
        agentVersion.getSubjects().add(subject2);
        agent.getVersions().add(agentVersion);
        Source source = PopulatorUtil.getSource();
        agent.setLocatedOn(source);
        agent.setSource(source);
        entityManager.persist(source);
        entityManager.persist(subject1);
        entityManager.persist(subject2);
        entityManager.persist(agentVersion);
        entityManager.persist(agent);

        Collection collection = (Collection) entityCreator.getNextRecord(Collection.class);
        collection.setUpdated(new Date());
        CollectionVersion collectionVersion = PopulatorUtil.getCollectionVersion(collection);
        Subject subject3 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject3);
        Subject subject4 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject4);
        collection.getVersions().add(collectionVersion);
        collection.getCollector().add(agent);
        agent.getCollectorOf().add(collection);
        collection.setLocatedOn(source);
        collection.setSource(source);
        entityManager.persist(collectionVersion);
        entityManager.persist(collection);

        Service service = (Service) entityCreator.getNextRecord(Service.class);
        service.setUpdated(new Date());
        ServiceVersion serviceVersion = PopulatorUtil.getServiceVersion(service);
        service.getVersions().add(serviceVersion);
        service.getSupportedBy().add(collection);
        collection.getSupports().add(service);
        service.setLocatedOn(source);
        service.setSource(source);
        entityManager.persist(serviceVersion);
        entityManager.persist(service);

        Activity activity = (Activity) entityCreator.getNextRecord(Activity.class);
        activity.setUpdated(new Date());
        ActivityVersion activityVersion = PopulatorUtil.getActivityVersion(activity);
        activity.getVersions().add(activityVersion);
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(agent);
        agent.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        activity.setLocatedOn(source);
        activity.setSource(source);
        entityManager.persist(activityVersion);
        entityManager.persist(activity);
        entityManager.getTransaction().commit();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    public void testGetEntryFromAgent() throws Exception {
        List<Agent> agents = agentDao.getAll();
        Agent agent = agents.get(0);
        AgentVersion agentVersion = agent.getVersions().first();
        Entry entry = AdapterHelper.getEntryFromEntity(agentVersion, true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey());
        assertEquals("Entry title", entry.getTitle(), agent.getTitle());
        assertEquals("Entry content", entry.getContent(), agent.getContent());
        assertEquals("Entry authors", entry.getAuthors().size(), agent.getAuthors().size());
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_IS_COLLECTOR_OF).size() >= 1);
    }

    @Test
    public void testGetEnteryFromCollection() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        CollectionVersion version = collection.getVersions().first();
        Entry entry = AdapterHelper.getEntryFromEntity(version, true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        assertEquals("Entry title", entry.getTitle(), collection.getTitle());
        assertEquals("Entry content", entry.getContent(), collection.getContent());
        assertEquals("Entry authors", entry.getAuthors().size(), collection.getAuthors().size());
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_PAGE).size() >= 1);
        //TODO this needs to be fixed after the new data model is fixed up
//        assertTrue("Entry should have at least one agent", entry.getLinks(Constants.REL_CREATOR).size() >= 1);
//        assertTrue("Entry should have at least one service", entry.getLinks(Constants.REL_IS_ACCESSED_VIA).size() >= 1);
//        assertTrue("Entry should have at least one activity", entry.getLinks(Constants.REL_IS_OUTPUT_OF).size() >= 1);
    }

    @Test
    public void testGetEnteryFromActivity() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(activity.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
        assertEquals("Entry title", entry.getTitle(), activity.getTitle());
        assertEquals("Entry content", entry.getContent(), activity.getContent());
        assertEquals("Entry authors", entry.getAuthors().size(), activity.getAuthors().size());
        assertTrue("Entry should have at least 2 categories", entry.getCategories().size() >= 2);
//TODO this needs to be fixed after the new data model is fixed up
//  assertTrue("Entry should have at least one agent", entry.getLinks(Constants.REL_HAS_PARTICIPANT).size() >= 1);
//        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_HAS_OUTPUT).size() >= 1);
    }

    @Test
    public void testGetEnteryFromService() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(service.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey());
        assertEquals("Entry title", entry.getTitle(), service.getTitle());
        assertEquals("Entry content", entry.getContent(), service.getContent());
        assertEquals("Entry authors", entry.getAuthors().size(), service.getAuthors().size());
        assertTrue("Entry should have at least 2 categories", entry.getCategories().size() >= 2);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_PAGE).size() >= 1);
        //TODO this needs to be fixed after the new data model is fixed up
//        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_IS_SUPPORTED_BY).size() >= 1);
    }

    @Test
    public void testUpdateAgentFromEntry() throws Exception {
        List<Agent> agents = agentDao.getAll();
        Agent agent = agents.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(agent.getVersions().first(), true);
        Version version = AdapterHelper.assembleAndValidateVersionFromEntry(agent, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", agent.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", agent.getVersions().first().getDescription(), version.getDescription());
    }

    @Test
    public void testUpdateCollectionFromEntry() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(collection.getVersions().first(), true);
        CollectionVersion version = (CollectionVersion) AdapterHelper.assembleAndValidateVersionFromEntry(collection, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", collection.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", collection.getVersions().first().getDescription(), version.getDescription());
        assertEquals("Entry location", collection.getVersions().first().getPages().iterator().next(), version.getPages().iterator().next());
    }

    @Test
    public void testUpdateServiceFromEntry() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(service.getVersions().first(), true);
        ServiceVersion version = (ServiceVersion) AdapterHelper.assembleAndValidateVersionFromEntry(service, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", service.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", service.getVersions().first().getDescription(), version.getDescription());
        assertEquals("Entry location", service.getVersions().first().getPages().iterator().next(), version.getPages().iterator().next());
    }

    @Test
    public void testUpdateActivityFromEntry() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(activity.getVersions().first(), true);
        Version version = AdapterHelper.assembleAndValidateVersionFromEntry(activity, entry);
        assertNotNull("Could not update entry", version);
        assertEquals("Entry title", activity.getTitle(), version.getTitle());
        assertEquals("Entry content", activity.getContent(), version.getDescription());
    }

}
