package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.Activity;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
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

    @Autowired
    private PartyDao partyDao;

    @Autowired
    private CollectionDao collectionDao;

    @Test
    public void testAddingService() throws Exception {
        int originalActivityTableSize = activityDao.getAll().size();
        Party party = PopulatorUtil.getPartyVersion();
        partyDao.save(party);
        Collection collection = PopulatorUtil.getCollectionVersion();
        collectionDao.save(collection);

        Activity activity = PopulatorUtil.getActivityVersion();
        activity.getHasOutput().add(collection);
        activity.getHasParticipant().add(party);
        party.getParticipantIn().add(activity);
        collection.getOutputOf().add(activity);
        activityDao.save(activity);
        collectionDao.update(collection);
        partyDao.update(party);

        activityDao.refresh(activity);
        assertEquals("Number of activities", activityDao.getAll().size(), (originalActivityTableSize + 1));
        assertEquals("Activity", activity, activityDao.getById(activity.getId()));
    }
}
