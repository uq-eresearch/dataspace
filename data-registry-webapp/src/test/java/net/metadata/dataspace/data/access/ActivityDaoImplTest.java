package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.version.ActivityVersion;
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

import static org.junit.Assert.assertTrue;

/**
 * Author: alabri
 * Date: 29/10/2010
 * Time: 10:32:48 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ActivityDaoImplTest {


    @Autowired
    private ActivityDao activityDao;

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
    public void testAddingActivity() throws Exception {
        Record activity = entityCreator.getNextRecord(Activity.class);
        activity.setUpdated(new Date());
        entityManager.getTransaction().begin();
        int originalTableSize = activityDao.getAll().size();
        ActivityVersion activityVersion = PopulatorUtil.getActivityVersion(activity);
        activity.getVersions().add(activityVersion);
//        Source source = PopulatorUtil.getSource();
//        activityVersion.setLocatedOn(source);
//        entityManager.persist(source);
        entityManager.persist(activityVersion);
        entityManager.persist(activity);
        entityManager.getTransaction().commit();

        Long id = activity.getId();
        Activity activityById = activityDao.getById(id);
        assertTrue("Table has " + activityDao.getAll().size() + " records", activityDao.getAll().size() == (originalTableSize + 1));
        Assert.assertEquals("Added and Retrieved records are not the same.", id, activityById.getId());
        Assert.assertEquals("Number of versions", 1, activityById.getVersions().size());
    }


    @Test
    public void testEditingActivity() throws Exception {
        testAddingActivity();
        assertTrue("Table is empty", activityDao.getAll().size() != 0);
        List<Activity> activities = activityDao.getAll();
        Activity activity = activities.get(0);
        entityManager.getTransaction().begin();
        Long id = activity.getId();
        Date now = new Date();
        String content = "Updated content";
        activity.getVersions().first().setDescription(content);
        activity.setUpdated(now);
        entityManager.merge(activity);
        entityManager.getTransaction().commit();
        Activity activityById = activityDao.getById(id);
        Assert.assertEquals("Modified and Retrieved records are not the same", activity, activityById);
        Assert.assertEquals("Update Date was not updated", now, activityById.getUpdated());
        Assert.assertEquals("content was not updated", content, activityById.getVersions().first().getDescription());
    }

    @Test
    public void testRemovingActivity() throws Exception {
        testAddingActivity();
        assertTrue("Table is empty", activityDao.getAll().size() != 0);
        List<Activity> activities = activityDao.getAll();
        for (Activity activity : activities) {
            activityDao.delete(activity);
        }
        assertTrue("Table is not empty", activityDao.getAll().size() == 0);
    }

    @Test
    public void testSoftDeleteActivity() throws Exception {
        testAddingActivity();
        assertTrue("Table is empty", activityDao.getAll().size() != 0);
        List<Activity> activities = activityDao.getAll();
        int updated = 0;
        for (Activity activity : activities) {
            updated = activityDao.softDelete(activity.getUriKey());
            activityDao.refresh(activity);
        }
        Assert.assertEquals("Updated rows", 1, updated);
        assertTrue("Table is empty", activityDao.getAll().size() != 0);
        Assert.assertEquals("Table has active records", 0, activityDao.getAllActive().size());
    }
}
