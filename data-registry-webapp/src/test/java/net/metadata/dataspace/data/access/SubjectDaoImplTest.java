package net.metadata.dataspace.data.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Subject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:22:16 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class SubjectDaoImplTest {

    @Autowired
    private SubjectDao subjectDao;

//    @Before
//    public void setUp() throws Exception {
//        PopulatorUtil.cleanup();
//    }

//    @After
//    public void tearDown() throws Exception {
//        PopulatorUtil.cleanup();
//    }

    @Test
    public void testAddingSubject() throws Exception {
        int originalSubjectTableSize = subjectDao.getAll().size();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);
        assertTrue("Subjects number should increase, Current: " + subjectDao.getAll().size() + " Original: " + originalSubjectTableSize, subjectDao.getAll().size() == (originalSubjectTableSize + 1));
    }

    @Test
    public void testEditingSubject() throws Exception {
//        testAddingSubject();
        Subject subject = subjectDao.getAll().get(0);
        Long id = subject.getId();
        String originalVocabUri = subject.getTerm();
        String newVocabURI = "subject/" + UUID.randomUUID().toString();
        subject.setTerm(newVocabURI);

        subjectDao.update(subject);
        Subject subjectByID = subjectDao.getById(id);
        assertEquals("Subject and SubjectByID are not the same", subject, subjectByID);
        assertEquals("Vocabulary URIs are not the same", newVocabURI, subjectByID.getTerm());
        assertFalse("Vocabulary URI should be updated " + originalVocabUri + " Current: " + subjectByID.getTerm(), originalVocabUri.equals(subjectByID.getTerm()));
    }

//    @Test
//    public void testDeletingSubjects () throws Exception {
//        testAddingSubject();
//        SubjectDao subjectDao = registryConfigurationImpl.getSubjectDao();
//
//        Subject subject = subjectDao.getAll().get(0);
//        long id = subject.getId();
//        subjectDao.delete(subject);
//
//        assertNull("This should return null", subjectDao.getById(id));
//
//    }
}
