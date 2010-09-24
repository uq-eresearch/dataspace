package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.model.PopulatorUtil;
import net.metadata.dataspace.model.Subject;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:22:16 PM
 */
public class SubjectDaoImplTest {
    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    @Test
    public void testAddingSubject() throws Exception {
        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
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
        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();

        Subject subject = subjectDao.getAll().get(0);
        Long id = subject.getId();
        String originalVocabUri = subject.getVocabularyURI();

        String newVocabURI = dataRegistryApplicationConfigurationImpl.getUriPrefix() + "subject/" + UUID.randomUUID().toString();
        subject.setVocabularyURI(newVocabURI);

        //update the subject in the database
        subjectDao.update(subject);

        Subject subjectByID = subjectDao.getById(id);

        assertEquals("Subject and SubjectByID are not the same", subject, subjectByID);
        assertEquals("Vocabulary URIs are not the same", newVocabURI, subjectByID.getVocabularyURI());
        assertFalse("Vocabulary URI should be updated " + originalVocabUri + " Current: " + subjectByID.getVocabularyURI(), originalVocabUri.equals(subjectByID.getVocabularyURI()));
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
