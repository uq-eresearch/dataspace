package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
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
//
//    @Autowired
//    private ServiceDao collectionDao;

    @Test
    public void testAddingService() throws Exception {
        int originalServiceTableSize = serviceDao.getAll().size();
        Service service = PopulatorUtil.getService();
        serviceDao.save(service);
        serviceDao.refresh(service);
        assertEquals("Number of services", serviceDao.getAll().size(), (originalServiceTableSize + 1));
        assertEquals("Service", service, serviceDao.getById(service.getId()));
    }
}
