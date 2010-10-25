package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:22:16 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class SubjectDaoImplTest {
    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();
    @Autowired
    private SubjectDao subjectDao;

    @Test
    public void testAddingSubject() throws Exception {
        int originalSubjectTableSize = subjectDao.getAll().size();

        //add a new subject
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        //Subject table shouldn't be empty
        assertTrue("Subjects number should increase Current: " + subjectDao.getAll().size() + " Original: " + originalSubjectTableSize, subjectDao.getAll().size() == (originalSubjectTableSize + 1));
    }

    @Test
    public void testEditingSubject() throws Exception {
        testAddingSubject();

        Subject subject = subjectDao.getAll().get(0);
        Long id = subject.getId();
        String originalVocabUri = subject.getVocabulary();

        String newVocabURI = dataRegistryApplicationConfigurationImpl.getUriPrefix() + "subject/" + UUID.randomUUID().toString();
        subject.setVocabulary(newVocabURI);

        //update the subject in the database
        subjectDao.update(subject);

        Subject subjectByID = subjectDao.getById(id);

        assertEquals("Subject and SubjectByID are not the same", subject, subjectByID);
        assertEquals("Vocabulary URIs are not the same", newVocabURI, subjectByID.getVocabulary());
        assertFalse("Vocabulary URI should be updated " + originalVocabUri + " Current: " + subjectByID.getVocabulary(), originalVocabUri.equals(subjectByID.getVocabulary()));
    }

//    @Test
//    public void testDeletingSubjects () throws Exception {
//        testAddingSubject();
//        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
//
//        Subject subject = subjectDao.getAll().get(0);
//        long id = subject.getId();
//        subjectDao.delete(subject);
//
//        assertNull("This should return null", subjectDao.getById(id));
//
//    }
}
