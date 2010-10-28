package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Subject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:23:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class PartyDaoImplTest {

    @Autowired
    private CollectionDao collectionDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private PartyDao partyDao;

    @After
    public void tearDown() throws Exception {
        //Remove all parties
        List<Collection> collectionList = collectionDao.getAll();
        for (Collection collection : collectionList) {
            collectionDao.delete(collection);
        }
    }

    @Test
    public void testAddingParty() throws Exception {
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        int originalPartyTableSize = partyDao.getAll().size();

        //Add a party
        Party party = PopulatorUtil.getParty();
        Set<Subject> subjects = new HashSet<Subject>();
        subjects.add(subject);
        party.setSubjects(subjects);
        //Add a collection
//        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();
        Set<Collection> collections = new HashSet<Collection>();
        Collection collection = PopulatorUtil.getCollection();
        Set<Party> collectors = collection.getCollector() == null ? new HashSet<Party>() : collection.getCollector();
        collectors.add(party);
        collection.setCollector(collectors);
        collections.add(collection);
        collectionDao.save(collection);

        party.setCollectorOf(collections);

        partyDao.save(party);

        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == (originalPartyTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", party, partyDao.getById(party.getId()));
    }


    @Test
    public void testEditingParty() throws Exception {
        testAddingParty();

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);

        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        List<Party> partyList = partyDao.getAll();
        Party party = partyList.get(0);
        Long id = party.getId();

        //Get original attribute values
        String originalPartyTitle = party.getTitle();
        String originalPartySummary = party.getSummary();

        //Edit attributes
        String newTitle = "New Title " + UUID.randomUUID().toString();
        party.setTitle(newTitle);
        String newSummary = "New Summary " + UUID.randomUUID().toString();
        party.setSummary(newSummary);

        //Update the party
        partyDao.update(party);

        Party partyById = partyDao.getById(id);

        assertEquals("Modified and Retrieved parties are not the same", party, partyById);
        assertTrue("Party title did not update, Current: " + partyById.getTitle() + " Expected: " + newTitle, partyById.getTitle().equals(newTitle));
        assertFalse("Party summary did not update: " + partyById.getSummary() + " Expected: " + newSummary, partyById.getSummary().equals(originalPartySummary));

    }


    @Test
    public void testRemovingParty() throws Exception {
        testAddingParty();

        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
        //Remove all parties
        List<Party> partyList = partyDao.getAll();
        for (Party party : partyList) {
            partyDao.delete(party);
        }
        //Check that party table has no records
        assertTrue("Party table should be empty. It has has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == 0);
    }

    @Test
    public void testSoftDeleteCollection() throws Exception {
        testAddingParty();
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
        List<Party> partyList = partyDao.getAll();
        int updated = 0;
        for (Party party : partyList) {
            updated = partyDao.softDelete(party.getUriKey());
            partyDao.refresh(party);
        }

        assertTrue("Updated rows should be 1 not " + updated, updated == 1);
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
        assertTrue("Party table has " + partyDao.getAllActive().size() + " records", partyDao.getAllActive().size() == 0);

        List<Party> parties = partyDao.getAll();
        Party party = parties.get(0);
        assertTrue("Collection isActive: " + party.isActive(), !party.isActive());
    }
}
