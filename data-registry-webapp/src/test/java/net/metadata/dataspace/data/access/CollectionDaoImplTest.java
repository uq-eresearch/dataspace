package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.Collection;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private PartyDao partyDao;

    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private JpaConnector jpaConnector;

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

        int originalCollectionTableSize = collectionDao.getAll().size();

        Collection collection = entityCreator.getNextCollection();
        jpaConnector.getEntityManager().persist(collection);


        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == (originalCollectionTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", collection, collectionDao.getById(collection.getId()));
    }


    @Test
    public void testEditingCollection() throws Exception {
        testAddingCollection();

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);

        List<Collection> collectionList = collectionDao.getAll();
        Collection collection = collectionList.get(0);
        Long id = collection.getId();
        Date now = new Date();
        collection.setUpdated(now);
        jpaConnector.getEntityManager().merge(collection);

        Collection collectionById = collectionDao.getById(id);

        assertEquals("Modified and Retrieved collections are not the same", collection, collectionById);
        assertTrue("Date", now.equals(collectionById.getUpdated()));
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
