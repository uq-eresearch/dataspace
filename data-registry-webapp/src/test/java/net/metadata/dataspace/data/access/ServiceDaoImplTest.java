package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 29/10/2010
 * Time: 10:33:06 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ServiceDaoImplTest {

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private CollectionDao collectionDao;

    @Test
    public void testAddingService() throws Exception {
        int originalServiceTableSize = serviceDao.getAll().size();
        Collection collection = PopulatorUtil.getCollectionVersion();
        collectionDao.save(collection);
        Service service = PopulatorUtil.getServiceVersion();
        service.getSupportedBy().add(collection);
        collection.getSupports().add(service);
        serviceDao.save(service);
        collectionDao.update(collection);

        assertEquals("Number of services", serviceDao.getAll().size(), (originalServiceTableSize + 1));
        assertEquals("Service", service, serviceDao.getById(service.getId()));
    }
}
