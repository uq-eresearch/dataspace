package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Service;
import net.metadata.dataspace.data.model.ServiceVersion;
import org.junit.After;
import org.junit.Assert;
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
    private JpaConnector jpaConnector;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingService() throws Exception {
        Service service = entityCreator.getNextService();
        service.setUpdated(new Date());
        entityManager.getTransaction().begin();
        int originalTableSize = serviceDao.getAll().size();
        ServiceVersion serviceVersion = PopulatorUtil.getServiceVersion(service);
        service.getVersions().add(serviceVersion);
        entityManager.persist(serviceVersion);
        entityManager.persist(service);
        entityManager.getTransaction().commit();

        Long id = service.getId();
        Service serviceById = serviceDao.getById(id);
        assertTrue("Table has " + serviceDao.getAll().size() + " records", serviceDao.getAll().size() == (originalTableSize + 1));
        Assert.assertEquals("Added and Retrieved records are not the same.", id, serviceById.getId());
        Assert.assertEquals("Number of versions", 1, serviceById.getVersions().size());
    }


    @Test
    public void testEditingService() throws Exception {
        testAddingService();
        assertTrue("Table is empty", serviceDao.getAll().size() != 0);
        List<Service> services = serviceDao.getAll();
        Service service = services.get(0);
        entityManager.getTransaction().begin();
        Long id = service.getId();
        Date now = new Date();
        String summary = "Updated Summary";
        service.getVersions().first().setSummary(summary);
        service.setUpdated(now);
        entityManager.merge(service);
        entityManager.getTransaction().commit();
        Service serviceById = serviceDao.getById(id);
        Assert.assertEquals("Modified and Retrieved parties are not the same", service, serviceById);
        Assert.assertEquals("Update Date was not updated", now, serviceById.getUpdated());
        Assert.assertEquals("Summary was not updated", summary, serviceById.getVersions().first().getSummary());
    }

    @Test
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
