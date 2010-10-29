package net.metadata.dataspace.data.access;

import junit.framework.Assert;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.Activity;
import net.metadata.dataspace.data.model.PopulatorUtil;
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

    @Test
    public void testAddingService() throws Exception {
        int originalActivityTableSize = activityDao.getAll().size();
        Activity activity = PopulatorUtil.getActivity();
        activityDao.save(activity);
        activityDao.refresh(activity);
        assertEquals("Number of activities", activityDao.getAll().size(), (originalActivityTableSize + 1));
        Assert.assertEquals("Activity", activity, activityDao.getById(activity.getId()));
    }
}
