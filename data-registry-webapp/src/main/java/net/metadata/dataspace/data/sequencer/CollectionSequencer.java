package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 2:01:19 PM
 */
public class CollectionSequencer extends AbstractAtomicSquencer {

    public CollectionSequencer(CollectionDao collectionDao) {
        Collection collection = collectionDao.getMostRecentInserted();
        if (collection == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            collectionDao.refresh(collection);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = collection.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}
