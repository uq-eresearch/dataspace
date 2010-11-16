package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:23:48 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ActivityAtomicSequencerTest {
    @Autowired
    private ActivityAtomicSequencer activityAtomicSequencer;

    @Autowired
    private EntityCreator entityCreator;

    @Test
    public void testNext() throws Exception {
        int currentNumber = activityAtomicSequencer.current();
        int next = activityAtomicSequencer.next();
        assertTrue("Current number should be smaller than next number.", currentNumber + 1 == next);
    }

    @Test
    public void testActivitySequencing() throws Exception {
        Record activity1 = entityCreator.getNextRecord(Activity.class);
        Record activity2 = entityCreator.getNextRecord(Activity.class);
        Record activity3 = entityCreator.getNextRecord(Activity.class);

        assertTrue("Atomic number should increase by 1.", activity1.getAtomicNumber() + 1 == activity2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", activity1.getAtomicNumber() + 2 == activity3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", activity2.getAtomicNumber() + 1 == activity3.getAtomicNumber());
    }
}
