package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:24:49 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class CollectionAtomicSequencerTest {
    @Autowired
    private CollectionAtomicSequencer collectionAtomicSequencer;

    @Test
    public void testNext() throws Exception {
        int currentNumber = collectionAtomicSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < collectionAtomicSequencer.next());
    }

}
