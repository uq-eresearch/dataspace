package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.util.DaoHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 11:17:47 AM
 */
public class AgentSequencer extends AbstractAtomicSquencer {

    public AgentSequencer(AgentDao agentDao) {
        Agent agent = agentDao.getMostRecentInserted();
        if (agent == null) {
            atomicInteger = new AtomicInteger(0);
        } else {
            agentDao.refresh(agent);
            final int BASE_THIRTY_ONE = 31;
            String uriKey = agent.getUriKey();
            atomicInteger = new AtomicInteger(DaoHelper.fromOtherBaseToDecimal(BASE_THIRTY_ONE, uriKey));
        }
    }
}
