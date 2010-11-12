package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 11:17:47 AM
 */
public class PartyAtomicSequencer extends AbstractAtomicSquencer {

    public PartyAtomicSequencer(PartyDao partyDao) {
        Party party = partyDao.getMostRecentInserted();
        if (party == null) {
            atomicInterger = new AtomicInteger(0);
        } else {
            partyDao.refresh(party);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = party.getUriKey();
            atomicInterger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }
}
