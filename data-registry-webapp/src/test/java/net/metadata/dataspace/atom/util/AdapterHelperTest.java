package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.*;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private EntityCreator entryCreator;

    @Before
    public void setUp() throws Exception {
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        Party party = PopulatorUtil.getParty();
        partyDao.save(party);
        party.getSubjects().add(subject);

        Collection collection = PopulatorUtil.getCollection();
        collectionDao.save(collection);
        collection.getSubjects().add(subject);
        collection.getCollector().add(party);
        party.getCollectorOf().add(collection);
        subjectDao.update(subject);
        collectionDao.update(collection);
        partyDao.update(party);

        Service service = PopulatorUtil.getService();
        service.getSupportedBy().add(collection);
        collection.getSupports().add(service);
        serviceDao.save(service);
        collectionDao.update(collection);

        Activity activity = PopulatorUtil.getActivity();
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(party);
        party.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        activityDao.save(activity);
        collectionDao.update(collection);
        partyDao.update(party);
    }

    @Test
    public void testGetEntryFromParty() throws Exception {
        List<Party> parties = partyDao.getAll();
        Party party = parties.get(0);
        Entry entry = AdapterHelper.getEntryFromParty(party);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.PARTIES_PATH + "/" + party.getUriKey());
        assertEquals("Entry title", entry.getTitle(), party.getTitle());
        assertEquals("Entry summary", entry.getSummary(), party.getSummary());
        assertEquals("Entry content", entry.getContent(), party.getContent());
        assertEquals("Entry updated", entry.getUpdated(), party.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), party.getAuthors().size());
        assertTrue("Entry should have at least one subject", entry.<Element>getExtensions(Constants.SUBJECT_QNAME).size() == 1);
        assertTrue("Entry should have at least one collection", entry.<Element>getExtensions(Constants.COLLECTOR_OF_QNAME).size() == 1);
    }

    @Test
    public void testGetEnteryFromCollection() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        Entry entry = AdapterHelper.getEntryFromCollection(collection);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey());
        assertEquals("Entry title", entry.getTitle(), collection.getTitle());
        assertEquals("Entry summary", entry.getSummary(), collection.getSummary());
        assertEquals("Entry content", entry.getContent(), collection.getContent());
        assertEquals("Entry updated", entry.getUpdated(), collection.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), collection.getAuthors().size());
        assertTrue("Entry should have at least one subject", entry.<Element>getExtensions(Constants.SUBJECT_QNAME).size() == 1);
        assertTrue("Entry should have at least one location", entry.<Element>getExtensions(Constants.LOCATION_QNAME).size() == 1);
        assertTrue("Entry should have at least one party", entry.<Element>getExtensions(Constants.COLLECTOR_QNAME).size() == 1);
        assertTrue("Entry should have at least one service", entry.<Element>getExtensions(Constants.SUPPORTS_QNAME).size() == 1);
        assertTrue("Entry should have at least one activity", entry.<Element>getExtensions(Constants.IS_OUTPUT_OF_QNAME).size() == 1);
    }

    @Test
    public void testGetEnteryFromActivity() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromActivity(activity);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + activity.getUriKey());
        assertEquals("Entry title", entry.getTitle(), activity.getTitle());
        assertEquals("Entry summary", entry.getSummary(), activity.getSummary());
        assertEquals("Entry content", entry.getContent(), activity.getContent());
        assertEquals("Entry updated", entry.getUpdated(), activity.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), activity.getAuthors().size());
        assertTrue("Entry should have at least one party", entry.<Element>getExtensions(Constants.HAS_PARTICIPANT_QNAME).size() == 1);
        assertTrue("Entry should have at least one collection", entry.<Element>getExtensions(Constants.HAS_OUTPUT_QNAME).size() == 1);
    }

    @Test
    public void testGetEnteryFromService() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromService(service);
        assertEquals("Entry id", entry.getId().toString(), Constants.ID_PREFIX + Constants.SERVICES_PATH + "/" + service.getUriKey());
        assertEquals("Entry title", entry.getTitle(), service.getTitle());
        assertEquals("Entry summary", entry.getSummary(), service.getSummary());
        assertEquals("Entry content", entry.getContent(), service.getContent());
        assertEquals("Entry updated", entry.getUpdated(), service.getUpdated());
        assertEquals("Entry authors", entry.getAuthors().size(), service.getAuthors().size());
        assertTrue("Entry should have at least one location", entry.<Element>getExtensions(Constants.LOCATION_QNAME).size() == 1);
        assertTrue("Entry should have at least one collection", entry.<Element>getExtensions(Constants.SUPPORTED_BY_QNAME).size() == 1);
    }

    @Test
    public void testUpdatePartyFromEntry() throws Exception {
        List<Party> parties = partyDao.getAll();
        Party party = parties.get(0);
        Entry entry = AdapterHelper.getEntryFromParty(party);
        Party newParty = entryCreator.getNextParty();
        assertTrue("Could not update party", AdapterHelper.updatePartyFromEntry(newParty, entry));
        assertEquals("Entry title", party.getTitle(), newParty.getTitle());
        assertEquals("Entry summary", party.getSummary(), newParty.getSummary());
        assertEquals("Entry content", party.getContent(), newParty.getContent());
        assertTrue("Entry updated", party.getUpdated().equals(newParty.getUpdated()));
    }

    @Test
    public void testUpdateCollectionFromEntry() throws Exception {
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        Entry entry = AdapterHelper.getEntryFromCollection(collection);
        Collection newCollection = entryCreator.getNextCollection();
        assertTrue("Could not update collection", AdapterHelper.updateCollectionFromEntry(newCollection, entry));
        assertEquals("Entry title", collection.getTitle(), newCollection.getTitle());
        assertEquals("Entry summary", collection.getSummary(), newCollection.getSummary());
        assertEquals("Entry content", collection.getContent(), newCollection.getContent());
        assertEquals("Entry location", collection.getLocation(), newCollection.getLocation());
        assertTrue("Entry updated", collection.getUpdated().equals(newCollection.getUpdated()));
    }

    @Test
    public void testUpdateServiceFromEntry() throws Exception {
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Entry entry = AdapterHelper.getEntryFromService(service);
        Service newService = entryCreator.getNextService();
        assertTrue("Could not update service", AdapterHelper.updateServiceFromEntry(newService, entry));
        assertEquals("Entry title", service.getTitle(), newService.getTitle());
        assertEquals("Entry summary", service.getSummary(), newService.getSummary());
        assertEquals("Entry content", service.getContent(), newService.getContent());
        assertEquals("Entry location", service.getLocation(), newService.getLocation());
        assertTrue("Entry updated", service.getUpdated().equals(newService.getUpdated()));
    }

    @Test
    public void testUpdateActivityFromEntry() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromActivity(activity);
        Activity newActivity = entryCreator.getNextActivity();
        assertTrue("Could not update activity", AdapterHelper.updateActivityFromEntry(newActivity, entry));
        assertEquals("Entry title", activity.getTitle(), newActivity.getTitle());
        assertEquals("Entry summary", activity.getSummary(), newActivity.getSummary());
        assertEquals("Entry content", activity.getContent(), newActivity.getContent());
        assertTrue("Entry updated", activity.getUpdated().equals(newActivity.getUpdated()));
    }

    @Test
    public void testGetUriKeysFromExtension() throws Exception {
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        Entry entry = AdapterHelper.getEntryFromActivity(activity);
        Element element = entry.addExtension(Constants.HAS_OUTPUT_QNAME);
        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        String uri = Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey();
        element.setAttributeValue("uri", uri);

        assertEquals("URI is not the same", collection.getUriKey(), AdapterHelper.getUriKeysFromExtension(entry, Constants.HAS_OUTPUT_QNAME).iterator().next());
    }
}
