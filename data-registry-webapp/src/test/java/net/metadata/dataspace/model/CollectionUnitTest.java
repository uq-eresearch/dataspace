package net.metadata.dataspace.model;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.SubjectDao;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:58:36 PM
 */
public class CollectionUnitTest {

    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    @Test
    public void testAddingCollection() throws Exception {
        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        CollectionDao collectionDao = dataRegistryApplicationConfigurationImpl.getCollectionDao();

        Collection collection = PopulatorUtil.getCollection();
        List<Subject> subjects = new ArrayList<Subject>();
        subjects.add(subject);
        collection.setSubjects(subjects);
        collectionDao.save(collection);

        //Collection table shouldn't be empty
        assertTrue("Collection table has " + collectionDao.getAll().size() + " records", collectionDao.getAll().size() != 0);
        assertEquals("Added and Retrieved collections are not the same.", collection, collectionDao.getByKey(collection.getKeyURI()));
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
        String keyUri = collection.getKeyURI();
        int originalSubjectListSize = collection.getSubjects().size();
        collection.getSubjects().add(subject);
        String originalKeyUri = collection.getKeyURI();
        collection.setDescription("http://dataspace.uq.edu.au/" + UUID.randomUUID().toString());

        collectionDao.update(collection);

        Collection collectionByKey = collectionDao.getByKey(keyUri);

        assertEquals("Modified and Retrieved collections are not the same", collection, collectionByKey);
        assertTrue("Number of subjects is " + collectionByKey.getSubjects().size() + " expected " + (originalSubjectListSize + 1), collectionByKey.getSubjects().size() == (originalSubjectListSize + 1));
        assertFalse("Key URI was not updated Original " + originalKeyUri + " Current: " + collectionByKey.getDescription(), originalKeyUri.equals(collectionByKey.getDescription()));

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
