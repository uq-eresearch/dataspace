package net.metadata.dataspace.data.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:24:10 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class CollectionDaoImplTest {

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private DaoManager daoManager;
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = daoManager.getEntityManagerSource().getEntityManager();
        PopulatorUtil.cleanup();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    @Transactional
    public void testAddingCollection() throws Exception {

        Collection collection = (Collection) entityCreator.getNextRecord(Collection.class);
        int originalTableSize = collectionDao.getAll().size();
        CollectionVersion collectionVersion = PopulatorUtil.getCollectionVersion(collection);
        Subject subject1 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject1);
        Subject subject2 = PopulatorUtil.getSubject();
        collectionVersion.getSubjects().add(subject2);
        collection.addVersion(collectionVersion);
        Source source = PopulatorUtil.getSource();
        collectionVersion.setSource(source);
        entityManager.persist(source);
        entityManager.persist(subject1);
        entityManager.persist(subject2);
        entityManager.persist(collectionVersion);
        entityManager.persist(collection);

        Long id = collection.getId();
        Collection collectionById = collectionDao.getById(id);
        assertTrue("Table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == (originalTableSize + 1));
        assertEquals("Added and Retrieved records are not the same.", id, collectionById.getId());
        assertEquals("Number of versions", 1, collectionById.getVersions().size());
        assertEquals("Number of subjects", 2, collectionById.getVersions().first().getSubjects().size());
    }


    @Test
    @Transactional
    public void testEditingCollection() throws Exception {
        testAddingCollection();
        Calendar editStart = Calendar.getInstance();
        assertTrue("Table is empty", collectionDao.getAll().size() != 0);
        List<Collection> collectionList = collectionDao.getAll();
        Collection collection = collectionList.get(0);
        Long id = collection.getId();
        String content = "Updated content";
        collection.getVersions().first().setDescription(content);
        entityManager.flush();
        Collection collectionById = collectionDao.getById(id);
        assertEquals("Modified and Retrieved records are not the same", collection, collectionById);
        Assert.assertTrue(String.format(
				"Update Date was not updated. %tFT%<tT.%<tN < %tFT%<tT.%<tN",
				collectionById.getUpdated(), editStart),
				collectionById.getUpdated().after(editStart));
        assertEquals("content was not updated", content, collectionById.getVersions().first().getDescription());
    }


    @Test
    @Transactional
    public void testRemovingAgent() throws Exception {
        testAddingCollection();
        assertTrue("Table is empty", collectionDao.getAll().size() != 0);
        List<Collection> collectionList = collectionDao.getAll();
        for (Collection collection : collectionList) {
            collectionDao.delete(collection);
        }
        assertTrue("Table is not empty", collectionDao.getAll().size() == 0);
    }

    @Test
    @Transactional
    public void testSoftDeleteCollection() throws Exception {
        testAddingCollection();
        assertTrue("Table is empty", collectionDao.getAll().size() != 0);
        List<Collection> collectionList = collectionDao.getAll();
        int updated = 0;
        for (Collection collection : collectionList) {
            updated = collectionDao.softDelete(collection.getUriKey());
            collectionDao.refresh(collection);
        }
        assertEquals("Updated rows", 1, updated);
        assertTrue("Table is empty", collectionDao.getAll().size() != 0);
        assertEquals("Table has active records", 0, collectionDao.getAllActive().size());
    }

}
