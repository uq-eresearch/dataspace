package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private JpaConnector jpaConnector;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = jpaConnector.getEntityManager();
    }

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
        Party party = entityCreator.getNextParty();
        PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
        partyVersion.setTitle("Test Party Title");
        partyVersion.setSummary("Test Party Summary");
        partyVersion.setContent("Test Party Content");
        partyVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Party Author");
        partyVersion.setAuthors(authors);
        party.setUpdated(new Date());


        Subject subject = PopulatorUtil.getSubject();
        entityManager.getTransaction().begin();
        entityManager.persist(subject);

        int originalPartyTableSize = partyDao.getAll().size();

        partyVersion.getSubjects().add(subject);

        //Add a collection
        Set<Collection> collections = new HashSet<Collection>();
        Collection collection = PopulatorUtil.getCollectionVersion();
        Set<Party> collectors = collection.getCollector() == null ? new HashSet<Party>() : collection.getCollector();
        collectors.add(party);
        collection.setCollector(collectors);
        collections.add(collection);
        entityManager.persist(collection);

        partyVersion.setCollectorOf(collections);

        entityManager.persist(partyVersion);
        entityManager.persist(party);
        entityManager.getTransaction().commit();
        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == (originalPartyTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", party, partyDao.getById(party.getId()));
    }


    @Test
    public void testEditingParty() throws Exception {
//        testAddingParty();
//
//        //Collection table shouldn't be empty
//        assertTrue("Collection table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
//
//        Subject subject = PopulatorUtil.getSubject();
//        subjectDao.save(subject);
//
//        List<Party> partyList = partyDao.getAll();
//        Party party = partyList.get(0);
//        Long id = party.getId();
//
//        //Get original attribute values
//        String originalPartySummary = party.getSummary();
//
//        //Edit attributes
//        String newTitle = "New Title " + UUID.randomUUID().toString();
//        party.setTitle(newTitle);
//        String newSummary = "New Summary " + UUID.randomUUID().toString();
//        party.setSummary(newSummary);
//
//        //Update the party
//        partyDao.update(party);
//
//        Party partyById = partyDao.getById(id);
//
//        assertEquals("Modified and Retrieved parties are not the same", party, partyById);
//        assertTrue("Party title did not update, Current: " + partyById.getTitle() + " Expected: " + newTitle, partyById.getTitle().equals(newTitle));
//        assertFalse("Party summary did not update: " + partyById.getSummary() + " Expected: " + newSummary, partyById.getSummary().equals(originalPartySummary));

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
