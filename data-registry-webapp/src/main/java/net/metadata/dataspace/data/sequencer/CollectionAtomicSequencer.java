package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 2:01:19 PM
 */
public class CollectionAtomicSequencer {

    private AtomicInteger atomicInterger;
    private final int BASE_THIRTY_ONE = 31;

    public CollectionAtomicSequencer(CollectionDao collectionDao) {
        Collection collection = collectionDao.getMostRecentInsertedCollection();
        if (collection == null) {
            atomicInterger = new AtomicInteger(1);
        } else {
            collectionDao.refresh(collection);
            String uriKey = collection.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey) + 1);
        }
    }

    public int next() {
        return atomicInterger.getAndIncrement();
    }

    public int current() {
        return atomicInterger.get();
    }

}
