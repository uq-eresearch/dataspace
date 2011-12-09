package net.metadata.dataspace.data.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Publication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: alabri
 * Date: 23/02/2011
 * Time: 4:53:22 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class PublicationDaoImplTest {

    @Autowired
    private PublicationDao publicationDao;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingPublication() throws Exception {
        int originalPublicationTableSize = publicationDao.getAll().size();
        Publication publication = PopulatorUtil.getPublication();
        publicationDao.save(publication);
        assertTrue("Publication number should increase Current: " + publicationDao.getAll().size() + " Original: " + originalPublicationTableSize, publicationDao.getAll().size() == (originalPublicationTableSize + 1));
    }

    @Test
    public void testEditingPublication() throws Exception {
        testAddingPublication();
        Publication publication = publicationDao.getAll().get(0);
        Long id = publication.getId();
        String originalPublicationUri = publication.getPublicationURI();
        String newPublicationURI = "publication/" + UUID.randomUUID().toString();
        publication.setPublicationURI(newPublicationURI);
        publicationDao.update(publication);
        Publication publicationByID = publicationDao.getById(id);
        assertEquals("Original entity and edited entity are not the same", publication, publicationByID);
        assertEquals("URI was not updated", newPublicationURI, publicationByID.getPublicationURI());
        assertFalse("URI should be updated to " + originalPublicationUri + " Current: " + publicationByID.getPublicationURI(), originalPublicationUri.equals(publicationByID.getPublicationURI()));
    }

}
