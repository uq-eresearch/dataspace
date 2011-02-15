package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Agent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:24:58 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class AgentAtomicSequencerTest {
    @Autowired
    private AgentAtomicSequencer agentAtomicSequencer;

    @Autowired
    private EntityCreator entityCreator;

    @Test
    public void testNext() throws Exception {
        int currentNumber = agentAtomicSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < agentAtomicSequencer.next());
    }

    @Test
    public void testAgentSequencing() throws Exception {
        Record agent1 = entityCreator.getNextRecord(Agent.class);
        Record agent2 = entityCreator.getNextRecord(Agent.class);
        Record agent3 = entityCreator.getNextRecord(Agent.class);
        assertTrue("Atomic number should increase by 1.", agent1.getAtomicNumber() + 1 == agent2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", agent1.getAtomicNumber() + 2 == agent3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", agent2.getAtomicNumber() + 1 == agent3.getAtomicNumber());

    }
}
