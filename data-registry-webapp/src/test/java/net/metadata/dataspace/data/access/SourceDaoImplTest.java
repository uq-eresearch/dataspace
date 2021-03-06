package net.metadata.dataspace.data.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Source;

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
 * Time: 4:53:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class SourceDaoImplTest {

    @Autowired
    private SourceDao sourceDao;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
    }

    @After
    public void tearDown() throws Exception {
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingSource() throws Exception {
        int originalSourceTableSize = sourceDao.getAll().size();
        Source source = PopulatorUtil.getSource();
        sourceDao.save(source);
        assertTrue("Source number should increase Current: " + sourceDao.getAll().size() + " Original: " + originalSourceTableSize, sourceDao.getAll().size() == (originalSourceTableSize + 1));
    }

    @Test
    public void testEditingSource() throws Exception {
        testAddingSource();
        Source source = sourceDao.getAll().get(0);
        Long id = source.getId();
        String originalSourceUri = source.getSourceURI();
        String newSourceURI = "source/" + UUID.randomUUID().toString();
        source.setSourceURI(newSourceURI);

        sourceDao.update(source);
        Source sourceByID = sourceDao.getById(id);
        assertEquals("Original entity and edited entity are not the same", source, sourceByID);
        assertEquals("URI was not updated", newSourceURI, sourceByID.getSourceURI());
        assertFalse("URI should be updated to " + originalSourceUri + " Current: " + sourceByID.getSourceURI(), originalSourceUri.equals(sourceByID.getSourceURI()));
    }

}
