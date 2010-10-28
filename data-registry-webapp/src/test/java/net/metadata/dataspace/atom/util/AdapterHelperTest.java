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
        collectionDao.update(collection);
        partyDao.update(party);

        Service service = PopulatorUtil.getService();
        service.getSupportedBy().add(collection);
        collection.getSupports().add(service);
        serviceDao.save(service);

        Activity activity = PopulatorUtil.getActivity();
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(party);
        party.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        activityDao.save(activity);
    }

//    @After
//    public void tearDown() throws Exception {
//        List<Party> parties = partyDao.getAll();
//        for (Party party : parties) {
//            party.getCollectorOf().removeAll(party.getCollectorOf());
//            party.getParticipantIn().removeAll(party.getParticipantIn());
//            party.getSubjects().removeAll(party.getSubjects());
//            partyDao.delete(party);
//        }
//
//        List<Collection> collectionList = collectionDao.getAll();
//        for (Collection collection : collectionList) {
//            collection.getCollector().removeAll(collection.getCollector());
//            collection.getOutputOf().removeAll(collection.getOutputOf());
//            collection.getSupports().removeAll(collection.getSupports());
//            collection.getSupports().removeAll(collection.getSubjects());
//            collectionDao.delete(collection);
//        }
//
//        List<Service> services = serviceDao.getAll();
//        for (Service service : services) {
//            service.getSupportedBy().removeAll(service.getSupportedBy());
//            serviceDao.delete(service);
//        }
//
//        List<Activity> activityList = activityDao.getAll();
//        for (Activity activity : activityList) {
//            activity.getHasParticipant().removeAll(activity.getHasParticipant());
//            activity.getHasOutput().removeAll(activity.getHasOutput());
//            activityDao.delete(activity);
//        }
//
//        List<Subject> list = subjectDao.getAll();
//        for (Subject subject : list) {
//            subjectDao.delete(subject);
//        }
//    }

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
        assertTrue("Entry updated", party.getUpdated().before(newParty.getUpdated()));
    }
}
