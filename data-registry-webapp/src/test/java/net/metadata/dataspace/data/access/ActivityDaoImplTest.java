package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static net.sf.json.test.JSONAssert.assertEquals;

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

    @Test
    public void testAddingService() throws Exception {
        int originalActivityTableSize = activityDao.getAll().size();
        Party party = entityCreator.getNextParty();
        jpaConnector.getEntityManager().persist(party);
        Collection collection = entityCreator.getNextCollection();
        jpaConnector.getEntityManager().persist(collection);

        Activity activity = entityCreator.getNextActivity();
        ActivityVersion activityVersion = PopulatorUtil.getActivityVersion(activity);
        activityVersion.getHasOutput().add(collection);
        activityVersion.getHasParticipant().add(party);
        activityVersion.setParent(activity);
        activity.getVersions().add(activityVersion);
        party.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        jpaConnector.getEntityManager().persist(activity);

        activityDao.refresh(activity);
        assertEquals("Number of activities", activityDao.getAll().size(), (originalActivityTableSize + 1));
        assertEquals("Activity", activity, activityDao.getById(activity.getId()));
    }
}
