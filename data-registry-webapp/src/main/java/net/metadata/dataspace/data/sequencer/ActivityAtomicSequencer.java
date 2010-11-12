package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 1:55:04 PM
 */
public class ActivityAtomicSequencer extends AbstractAtomicSquencer {

    public ActivityAtomicSequencer(ActivityDao activityDao) {
        Activity activity = activityDao.getMostRecentInserted();
        if (activity == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            activityDao.refresh(activity);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = activity.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}

