package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.model.Party;
import net.metadata.dataspace.model.PopulatorUtil;
import net.metadata.dataspace.model.Subject;
import org.junit.Test;

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
public class PartyDaoImplTest {

    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    @Test
    public void testAddingParty() throws Exception {
        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);


        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();

        int originalPartyTableSize = partyDao.getAll().size();

        //Add a party
        Party party = PopulatorUtil.getParty();
        Set<Subject> subjects = new HashSet<Subject>();
        subjects.add(subject);
        party.setSubjects(subjects);

        //Add a collection
        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();
        Set<Collection> collections = new HashSet<Collection>();
        Collection collection = PopulatorUtil.getCollection();
        Set<Party> collector = collection.getCollector() == null ? new HashSet<Party>() : collection.getCollector();
        collector.add(party);
        collection.setCollector(collector);
        collectionDao.save(collection);
        collections.add(collection);

        party.setCollectorof(collections);

        partyDao.save(party);

        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == (originalPartyTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", party, partyDao.getById(party.getId()));
    }


    @Test
    public void testEditingParty() throws Exception {
        testAddingParty();

        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();
        //Collection table shouldn't be empty
        assertTrue("Collection table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);

        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
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
    public void testRemovingCollection() throws Exception {
        testAddingParty();

        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();

        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
        //Remove all collections
        List<Party> partyList = partyDao.getAll();
        for (Party party : partyList) {
            partyDao.delete(party);
        }
        //Check that collection table has no records
        assertTrue("Party table should be empty. It has has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == 0);
    }
}
