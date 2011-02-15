package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:25:10 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ServiceAtomicSequencerTest {
    @Autowired
    private ServiceAtomicSequencer serviceAtomicSequencer;

    @Autowired
    private EntityCreator entityCreator;

    @Test
    public void testNext() throws Exception {
        int currentNumber = serviceAtomicSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < serviceAtomicSequencer.next());
    }

    @Test
    public void testServiceSequencing() throws Exception {
        Record service1 = entityCreator.getNextRecord(Service.class);
        Record service2 = entityCreator.getNextRecord(Service.class);
        Record service3 = entityCreator.getNextRecord(Service.class);

        assertTrue("Atomic number should increase by 1.", service1.getAtomicNumber() + 1 == service2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", service1.getAtomicNumber() + 2 == service3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", service2.getAtomicNumber() + 1 == service3.getAtomicNumber());
    }
}
