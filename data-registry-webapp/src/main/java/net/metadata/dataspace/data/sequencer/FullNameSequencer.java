package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.FullNameDao;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: alabri
 * Date: 15/04/11
 * Time: 4:56 PM
 */
public class FullNameSequencer extends AbstractAtomicSquencer {

    public FullNameSequencer(FullNameDao fullNameDao) {
        FullName fullName = fullNameDao.getMostRecentInserted();
        if (fullName == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            //fullNameDao.refresh(fullName);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = fullName.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}
