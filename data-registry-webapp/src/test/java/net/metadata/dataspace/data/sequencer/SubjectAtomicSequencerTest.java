package net.metadata.dataspace.data.sequencer;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 26/10/2010
 * Time: 3:38:41 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class SubjectAtomicSequencerTest {

    @Autowired
    private SubjectSequencer subjectSequencer;

    @Autowired
    private EntityCreator entityCreaotr;

    @Test
    public void testNext() throws Exception {
        int currentNumber = subjectSequencer.current();
        assertTrue("Current number should be smaller than next number.", currentNumber < subjectSequencer.next());
    }

    @Test
    public void testSubjectSequencing() throws Exception {
        Subject subject1 = entityCreaotr.getNextSubject();
        Subject subject2 = entityCreaotr.getNextSubject();
        Subject subject3 = entityCreaotr.getNextSubject();
        assertTrue("Atomic number should increase by 1.", subject1.getAtomicNumber() + 1 == subject2.getAtomicNumber());
        assertTrue("Atomic number should increase by 2.", subject1.getAtomicNumber() + 2 == subject3.getAtomicNumber());
        assertTrue("Atomic number should increase by 1.", subject2.getAtomicNumber() + 1 == subject3.getAtomicNumber());
    }
}
