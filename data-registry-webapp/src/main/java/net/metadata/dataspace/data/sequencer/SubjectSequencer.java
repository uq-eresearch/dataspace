package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 2:01:04 PM
 */
public class SubjectSequencer extends AbstractAtomicSquencer {

    public SubjectSequencer(SubjectDao subjectDao) {
        Subject subject = subjectDao.getMostRecentInserted();
        if (subject == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            subjectDao.refresh(subject);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = subject.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}
