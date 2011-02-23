package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.app.RegistryConfiguration;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.context.Source;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Author: alabri
 * Date: 23/02/2011
 * Time: 4:53:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class SourceDaoImplTest {

    @Autowired
    private RegistryConfiguration registryConfigurationImpl;
    @Autowired
    private SourceDao sourceDao;

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
        String newSourceURI = registryConfigurationImpl.getUriPrefix() + "source/" + UUID.randomUUID().toString();
        source.setSourceURI(newSourceURI);

        sourceDao.update(source);
        Source sourceByID = sourceDao.getById(id);
        assertEquals("Original entity and edited entity are not the same", source, sourceByID);
        assertEquals("URI was not updated", newSourceURI, sourceByID.getSourceURI());
        assertFalse("URI should be updated to " + originalSourceUri + " Current: " + sourceByID.getSourceURI(), originalSourceUri.equals(sourceByID.getSourceURI()));
    }

}
