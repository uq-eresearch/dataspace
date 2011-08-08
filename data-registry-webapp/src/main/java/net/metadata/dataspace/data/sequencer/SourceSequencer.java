package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:44:23 PM
 */
public class SourceSequencer extends AbstractAtomicSquencer {

    public SourceSequencer(SourceDao sourceDao) {
        Source source = sourceDao.getMostRecentInserted();
        if (source == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            //sourceDao.refresh(source);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = source.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}

