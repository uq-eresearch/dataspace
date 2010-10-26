package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.Subject;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 2:01:04 PM
 */
public class SubjectAtomicSequencer {
    private AtomicInteger atomicInterger;
    private final int BASE_THIRTY_ONE = 31;

    public SubjectAtomicSequencer(SubjectDao subjectDao) {
        Subject subject = subjectDao.getMostRecentInsertedCollection();
        if (subject == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            subjectDao.refresh(subject);
            String uriKey = subject.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey) + 1);
        }
    }

    public int next() {
        return atomicInterger.incrementAndGet();
    }

    public int current() {
        return atomicInterger.get();
    }
}
