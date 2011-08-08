package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.PublicationDao;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:44:13 PM
 */
public class PublicationSequencer extends AbstractAtomicSquencer {

    public PublicationSequencer(PublicationDao publicationDao) {
        Publication publication = publicationDao.getMostRecentInserted();
        if (publication == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            //publicationDao.refresh(publication);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = publication.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}

