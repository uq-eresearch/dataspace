package net.metadata.dataspace.model;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.SubjectDao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        collectionDao.save(collection);

        assertEquals(collection, collectionDao.getByKey(collection.getKeyURI()));
    }

}
