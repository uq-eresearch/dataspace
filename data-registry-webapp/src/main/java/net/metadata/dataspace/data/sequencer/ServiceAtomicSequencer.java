package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Service;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 1:11:38 PM
 */
public class ServiceAtomicSequencer extends AbstractAtomicSquencer {

    public ServiceAtomicSequencer(ServiceDao serviceDao) {
        Service service = serviceDao.getMostRecentInserted();
        if (service == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            serviceDao.refresh(service);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = service.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }

}
