package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.DescriptionDao;
import net.metadata.dataspace.data.model.context.Description;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: alabri
 * Date: 23/02/2011
 * Time: 11:01:10 AM
 */
public class DescriptionSequencer extends AbstractAtomicSquencer {

    public DescriptionSequencer(DescriptionDao descriptionDao) {
        Description description = descriptionDao.getMostRecentInserted();
        if (description == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            descriptionDao.refresh(description);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = description.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}

