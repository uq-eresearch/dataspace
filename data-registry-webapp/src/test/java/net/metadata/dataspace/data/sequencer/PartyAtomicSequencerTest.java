package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Party;
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
public class PartyAtomicSequencerTest {
    @Autowired
    private PartyAtomicSequencer partyAtomicSequencer;

    @Autowired
    private EntityCreator entityCreator;

    @Test
    public void testNext() throws Exception {
        int currentNumber = partyAtomicSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < partyAtomicSequencer.next());
    }

    @Test
    public void testPartySequencing() throws Exception {
        Record party1 = entityCreator.getNextRecord(Party.class);
        Record party2 = entityCreator.getNextRecord(Party.class);
        Record party3 = entityCreator.getNextRecord(Party.class);
        assertTrue("Atomic number should increase by 1.", party1.getAtomicNumber() + 1 == party2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", party1.getAtomicNumber() + 2 == party3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", party2.getAtomicNumber() + 1 == party3.getAtomicNumber());

    }
}
