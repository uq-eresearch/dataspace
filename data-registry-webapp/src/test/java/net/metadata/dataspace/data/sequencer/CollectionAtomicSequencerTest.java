package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Collection;
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

    @Autowired
    private EntityCreator entityCreator;

    @Test
    public void testNext() throws Exception {
        int currentNumber = collectionAtomicSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < collectionAtomicSequencer.next());
    }

    @Test
    public void testCollectionSequencing() throws Exception {
        Collection collection1 = entityCreator.getNextCollection();
        Collection collection2 = entityCreator.getNextCollection();
        Collection collection3 = entityCreator.getNextCollection();
        assertTrue("Atomic number should increase by 1.", collection1.getAtomicNumber() + 1 == collection2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", collection1.getAtomicNumber() + 2 == collection3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", collection2.getAtomicNumber() + 1 == collection3.getAtomicNumber());
    }

}
