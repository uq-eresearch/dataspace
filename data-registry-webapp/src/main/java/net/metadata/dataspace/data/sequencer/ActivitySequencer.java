package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 1:55:04 PM
 */
public class ActivitySequencer extends AbstractAtomicSquencer {

    public ActivitySequencer(ActivityDao activityDao) {
        Activity activity = activityDao.getMostRecentInserted();
        if (activity == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            //activityDao.refresh(activity);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = activity.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}

