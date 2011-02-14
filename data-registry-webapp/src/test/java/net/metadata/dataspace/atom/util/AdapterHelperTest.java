package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.base.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.Entry;
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
    private SubjectDao subjectDao;
    @Autowired
    private PartyDao partyDao;
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
//        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();

        entityManager.getTransaction().begin();
        Party party = (Party) entityCreator.getNextRecord(Party.class);
        party.setUpdated(new Date());
        PartyVersion partyVersion = PopulatorUtil.getPartyVersion(party);
        partyVersion.getSubjects().add(PopulatorUtil.getSubject());
        partyVersion.getSubjects().add(PopulatorUtil.getSubject());
        party.getVersions().add(partyVersion);
        entityManager.persist(partyVersion);
        entityManager.persist(party);

        Collection collection = (Collection) entityCreator.getNextRecord(Collection.class);
        collection.setUpdated(new Date());
        CollectionVersion collectionVersion = PopulatorUtil.getCollectionVersion(collection);
        collectionVersion.getSubjects().add(PopulatorUtil.getSubject());
        collectionVersion.getSubjects().add(PopulatorUtil.getSubject());
        collection.getVersions().add(collectionVersion);
        collection.getCollector().add(party);
        party.getCollectorOf().add(collection);
        entityManager.persist(collectionVersion);
        entityManager.persist(collection);

        Service service = (Service) entityCreator.getNextRecord(Service.class);
        service.setUpdated(new Date());
        ServiceVersion serviceVersion = PopulatorUtil.getServiceVersion(service);
        service.getVersions().add(serviceVersion);
        service.getSupportedBy().add(collection);
        collection.getSupports().add(service);
        entityManager.persist(serviceVersion);
        entityManager.persist(service);

        Activity activity = (Activity) entityCreator.getNextRecord(Activity.class);
        activity.setUpdated(new Date());
        ActivityVersion activityVersion = PopulatorUtil.getActivityVersion(activity);
        activity.getVersions().add(activityVersion);
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(party);
        party.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        entityManager.persist(activityVersion);
        entityManager.persist(activity);
        entityManager.getTransaction().commit();
    }

    @Test
    public void testGetEntryFromParty() throws Exception {
        List<Party> parties = partyDao.getAll();
        Party party = parties.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(party.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + party.getUriKey());
        assertEquals("Entry title", entry.getTitle(), party.getTitle());
        assertEquals("Entry content", entry.getContent(), party.getContent());
        assertEquals("Entry updated", entry.getUpdated(), party.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), party.getAuthors().size());
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_IS_COLLECTOR_OF).size() >= 1);
    }

    @Test
    public void testGetEnteryFromCollection() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(collection.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        assertEquals("Entry title", entry.getTitle(), collection.getTitle());
        assertEquals("Entry content", entry.getContent(), collection.getContent());
        assertEquals("Entry updated", entry.getUpdated(), collection.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), collection.getAuthors().size());
        assertTrue("Entry should have at least 3 categories", entry.getCategories().size() > 2);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_IS_LOCATED_AT).size() >= 1);
        assertTrue("Entry should have at least one party", entry.getLinks(Constants.REL_CREATOR).size() >= 1);
        assertTrue("Entry should have at least one service", entry.getLinks(Constants.REL_IS_ACCESSED_VIA).size() >= 1);
        assertTrue("Entry should have at least one activity", entry.getLinks(Constants.REL_IS_OUTPUT_OF).size() >= 1);
    }

    @Test
    public void testGetEnteryFromActivity() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(activity.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
        assertEquals("Entry title", entry.getTitle(), activity.getTitle());
        assertEquals("Entry content", entry.getContent(), activity.getContent());
        assertEquals("Entry updated", entry.getUpdated(), activity.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), activity.getAuthors().size());
        assertTrue("Entry should have at least 2 categories", entry.getCategories().size() >= 2);
        assertTrue("Entry should have at least one party", entry.getLinks(Constants.REL_HAS_PARTICIPANT).size() == 1);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_HAS_OUTPUT).size() == 1);
    }

    @Test
    public void testGetEnteryFromService() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(service.getVersions().first(), true);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey());
        assertEquals("Entry title", entry.getTitle(), service.getTitle());
        assertEquals("Entry content", entry.getContent(), service.getContent());
        assertEquals("Entry updated", entry.getUpdated(), service.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), service.getAuthors().size());
        assertTrue("Entry should have at least 2 categories", entry.getCategories().size() >= 2);
        assertTrue("Entry should have at least one location", entry.getLinks(Constants.REL_IS_LOCATED_AT).size() == 1);
        assertTrue("Entry should have at least one collection", entry.getLinks(Constants.REL_IS_SUPPORTED_BY).size() == 1);
    }

    @Test
    public void testUpdatePartyFromEntry() throws Exception {
        List<Party> parties = partyDao.getAll();
        Party party = parties.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(party.getVersions().first(), true);
        Version version = entityCreator.getNextVersion(party);
        assertTrue("Could not update entry", AdapterHelper.isValidVersionFromEntry(version, entry));
        assertEquals("Entry title", party.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", party.getVersions().first().getDescription(), version.getDescription());
        assertTrue("Entry updated", party.getVersions().first().getUpdated().equals(version.getUpdated()));
    }

    @Test
    public void testUpdateCollectionFromEntry() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(collection.getVersions().first(), true);
        CollectionVersion version = (CollectionVersion) entityCreator.getNextVersion(collection);
        assertTrue("Could not update entry", AdapterHelper.isValidVersionFromEntry(version, entry));
        assertEquals("Entry title", collection.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", collection.getVersions().first().getDescription(), version.getDescription());
        assertEquals("Entry location", collection.getVersions().first().getLocation(), version.getLocation());
        assertTrue("Entry updated", collection.getVersions().first().getUpdated().equals(version.getUpdated()));
    }

    @Test
    public void testUpdateServiceFromEntry() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(service.getVersions().first(), true);
        ServiceVersion version = (ServiceVersion) entityCreator.getNextVersion(service);
        assertTrue("Could not update entry", AdapterHelper.isValidVersionFromEntry(version, entry));
        assertEquals("Entry title", service.getVersions().first().getTitle(), version.getTitle());
        assertEquals("Entry content", service.getVersions().first().getDescription(), version.getDescription());
        assertEquals("Entry location", service.getVersions().first().getLocation(), version.getLocation());
        assertTrue("Entry updated", service.getVersions().first().getUpdated().equals(version.getUpdated()));
    }

    @Test
    public void testUpdateActivityFromEntry() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromEntity(activity.getVersions().first(), true);
        Version version = entityCreator.getNextVersion(activity);
        assertTrue("Could not update entry", AdapterHelper.isValidVersionFromEntry(version, entry));
        assertEquals("Entry title", activity.getTitle(), version.getTitle());
        assertEquals("Entry content", activity.getContent(), version.getDescription());
        assertTrue("Entry updated", activity.getUpdated().equals(version.getUpdated()));
    }

}
