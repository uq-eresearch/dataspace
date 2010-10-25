package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Subject;
import org.junit.After;
import org.junit.Before;
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
 * Time: 3:24:10 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class CollectionDaoImplTest {

    @Autowired
    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl;
    @Autowired
    private CollectionDao collectionDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private PartyDao partyDao;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        //Remove all collections
        List<Collection> collectionList = collectionDao.getAll();
        for (Collection collection : collectionList) {
            collectionDao.delete(collection);
        }
    }

    @Test
    public void testAddingCollection() throws Exception {

        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        int originalCollectionTableSize = collectionDao.getAll().size();

        Collection collection = PopulatorUtil.getCollection();
        Set<Subject> subjects = new HashSet<Subject>();
        subjects.add(subject);
        collection.setSubjects(subjects);


        Party party = PopulatorUtil.getParty();
        partyDao.save(party);
        Set<Party> collectors = collection.getCollector() == null ? new HashSet<Party>() : collection.getCollector();
        collectors.add(party);
        collection.setCollector(collectors);

        collectionDao.save(collection);


        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == (originalCollectionTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", collection, collectionDao.getById(collection.getId()));
    }


    @Test
    public void testEditingCollection() throws Exception {
        testAddingCollection();

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);

        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        List<Collection> collectionList = collectionDao.getAll();
        Collection collection = collectionList.get(0);
        Long id = collection.getId();
        int originalSubjectListSize = collection.getSubjects().size();
        collection.getSubjects().add(subject);
        String originalLocationUri = collection.getLocation();
        collection.setLocation(dataRegistryApplicationConfigurationImpl.getUriPrefix() + "collection/" + UUID.randomUUID().toString());

        collectionDao.update(collection);

        Collection collectionById = collectionDao.getById(id);

        assertEquals("Modified and Retrieved collections are not the same", collection, collectionById);
        assertTrue("The number of subjects should increase Current: " + collectionById.getSubjects().size() + " Expected: " + (originalSubjectListSize + 1), collectionById.getSubjects().size() == (originalSubjectListSize + 1));
        assertFalse("Location URI should be updated " + originalLocationUri + " Current: " + collectionById.getLocation(), originalLocationUri.equals(collectionById.getLocation()));

    }

    @Test
    public void testRemovingCollection() throws Exception {
        testAddingCollection();

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);
        //Remove all collections
        List<Collection> collectionList = collectionDao.getAll();
        for (Collection collection : collectionList) {
            collectionDao.delete(collection);
        }
        //Check that collection table has no records
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == 0);
    }

    @Test
    public void testSoftDeleteCollection() throws Exception {
        testAddingCollection();

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);
        //Remove all collections
        List<Collection> collectionList = collectionDao.getAll();
        int updated = 0;
        for (Collection collection : collectionList) {
            updated = collectionDao.softDelete(collection.getUriKey());
            collectionDao.refresh(collection);
        }

        assertTrue("Updated rows should be 1 not " + updated, updated == 1);
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);
        assertTrue("Collection table has " + collectionDao.getAllActive().size() + " records", collectionDao.getAllActive().size() == 0);

        List<Collection> collections = collectionDao.getAll();
        Collection collection = collections.get(0);
        assertTrue("Collection isActive: " + collection.isActive(), !collection.isActive());

    }

}
