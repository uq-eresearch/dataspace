package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.model.PopulatorUtil;
import net.metadata.dataspace.model.Subject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:24:10 PM
 */
public class CollectionDaoImplTest {
    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    @Test
    public void testAddingCollection() throws Exception {

        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();

        int originalCollectionTableSize = collectionDao.getAll().size();

        Collection collection = PopulatorUtil.getCollection();
        List<Subject> subjects = new ArrayList<Subject>();
        subjects.add(subject);
        collection.setSubjects(subjects);
        collectionDao.save(collection);

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() == (originalCollectionTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", collection, collectionDao.getById(collection.getId()));
    }


    @Test
    public void testEditingCollection() throws Exception {
        testAddingCollection();

        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();
        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);

        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        List<Collection> collectionList = collectionDao.getAll();
        Collection collection = collectionList.get(0);
        String id = collection.getId();
        int originalSubjectListSize = collection.getSubjects().size();
        collection.getSubjects().add(subject);
        String originalLocationUri = collection.getLocationURI();
        collection.setLocationURI(dataRegistryApplicationConfigurationImpl.getUriPrefix() + "collection/" + UUID.randomUUID().toString());

        collectionDao.update(collection);

        Collection collectionById = collectionDao.getById(id);

        assertEquals("Modified and Retrieved collections are not the same", collection, collectionById);
        assertTrue("The number of subjects should increase Current: " + collectionById.getSubjects().size() + " Expected: " + (originalSubjectListSize + 1), collectionById.getSubjects().size() == (originalSubjectListSize + 1));
        assertFalse("Location URI should be updated " + originalLocationUri + " Current: " + collectionById.getLocationURI(), originalLocationUri.equals(collectionById.getLocationURI()));

    }

    @Test
    public void testRemovingCollection() throws Exception {
        testAddingCollection();

        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();

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
}
