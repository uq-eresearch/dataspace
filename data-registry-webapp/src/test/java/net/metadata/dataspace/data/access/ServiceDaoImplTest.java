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
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ServiceVersion;

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
    private EntityCreator entityCreator;

    @Autowired
    private DaoManager daoManager;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
        entityManager = daoManager.getEntityManagerSource().getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    @Transactional
    public void testAddingService() throws Exception {
        Service service = (Service) entityCreator.getNextRecord(Service.class);
        int originalTableSize = serviceDao.getAll().size();
        ServiceVersion serviceVersion = PopulatorUtil.getServiceVersion(service);
        service.addVersion(serviceVersion);
        Source source = PopulatorUtil.getSource();
        serviceVersion.setSource(source);
        entityManager.persist(source);
        entityManager.persist(serviceVersion);
        entityManager.persist(service);

        Long id = service.getId();
        Service serviceById = serviceDao.getById(id);
        assertTrue("Table has " + serviceDao.getAll().size() + " records", serviceDao.getAll().size() == (originalTableSize + 1));
        Assert.assertEquals("Added and Retrieved records are not the same.", id, serviceById.getId());
        Assert.assertEquals("Number of versions", 1, serviceById.getVersions().size());
    }


    @Test
    @Transactional
    public void testEditingService() throws Exception {
        testAddingService();
        Calendar editStart = Calendar.getInstance();
        assertTrue("Table is empty", serviceDao.getAll().size() != 0);
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        Long id = service.getId();
        String content = "Updated content";
        service.getVersions().first().setDescription(content);
        entityManager.flush();
        Service serviceById = serviceDao.getById(id);
        Assert.assertEquals("Modified and Retrieved records are not the same", service, serviceById);
        Assert.assertTrue(String.format(
				"Update Date was not updated. %tFT%<tT.%<tN < %tFT%<tT.%<tN",
				serviceById.getUpdated(), editStart),
        		serviceById.getUpdated().after(editStart));
        Assert.assertEquals("content was not updated", content, serviceById.getVersions().first().getDescription());
    }

    @Test
    @Transactional
    public void testRemovingService() throws Exception {
        testAddingService();
        assertTrue("Table is empty", serviceDao.getAll().size() != 0);
        List<Service> services = serviceDao.getAll();
        for (Service service : services) {
            serviceDao.delete(service);
        }
        assertTrue("Table is not empty", serviceDao.getAll().size() == 0);
    }

    @Test
    @Transactional
    public void testSoftDeleteService() throws Exception {
        testAddingService();
        assertTrue("Table is empty", serviceDao.getAll().size() != 0);
        List<Service> services = serviceDao.getAll();
        int updated = 0;
        for (Service service : services) {
            updated = serviceDao.softDelete(service.getUriKey());
            serviceDao.refresh(service);
        }
        assertEquals("Updated rows", 1, updated);
        assertTrue("Table is empty", serviceDao.getAll().size() != 0);
        assertEquals("Table has active records", 0, serviceDao.getAllActive().size());
    }

}
