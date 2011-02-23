package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private SubjectDao subjectDao;

    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private JpaConnector jpaConnector;
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
//        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingCollection() throws Exception {

        Record collection = entityCreator.getNextRecord(Collection.class);
        collection.setUpdated(new Date());
        entityManager.getTransaction().begin();
        int originalTableSize = collectionDao.getAll().size();
        CollectionVersion collectionVersion = PopulatorUtil.getCollectionVersion(collection);
        collectionVersion.getSubjects().add(PopulatorUtil.getSubject());
        collectionVersion.getSubjects().add(PopulatorUtil.getSubject());
        collection.getVersions().add(collectionVersion);
//        Source source = PopulatorUtil.getSource();
//        collectionVersion.setLocatedOn(source);
//        entityManager.persist(source);
        entityManager.persist(collectionVersion);
        entityManager.persist(collection);
        entityManager.getTransaction().commit();

        Long id = collection.getId();
        Collection collectionById = collectionDao.getById(id);
        assertTrue("Table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == (originalTableSize + 1));
        assertEquals("Added and Retrieved records are not the same.", id, collectionById.getId());
        assertEquals("Number of versions", 1, collectionById.getVersions().size());
        assertEquals("Number of subjects", 2, collectionById.getVersions().first().getSubjects().size());
    }


    @Test
    public void testEditingCollection() throws Exception {
        testAddingCollection();
        assertTrue("Table is empty", collectionDao.getAll().size() != 0);
        List<Collection> collectionList = collectionDao.getAll();
        Collection collection = collectionList.get(0);
        entityManager.getTransaction().begin();
        Long id = collection.getId();
        Date now = new Date();
        collection.setUpdated(now);
        String content = "Updated content";
        collection.getVersions().first().setDescription(content);
        collection.setUpdated(now);
        entityManager.merge(collection);
        entityManager.getTransaction().commit();
        Collection collectionById = collectionDao.getById(id);
        assertEquals("Modified and Retrieved records are not the same", collection, collectionById);
        assertEquals("Update Date was not updated", now, collectionById.getUpdated());
        assertEquals("content was not updated", content, collectionById.getVersions().first().getDescription());
    }


    @Test
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
