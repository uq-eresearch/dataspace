package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 11:17:47 AM
 */
public class PartyAtomicSequencer {

    private AtomicInteger atomicInterger;
    private final int BASE_THIRTY_ONE = 31;

    public PartyAtomicSequencer(PartyDao partyDao) {
        Party party = partyDao.getMostRecentInsertedParty();
        if (party == null) {
            atomicInterger = new AtomicInteger(1);
        } else {
            partyDao.refresh(party);
            String uriKey = party.getUriKey();
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
