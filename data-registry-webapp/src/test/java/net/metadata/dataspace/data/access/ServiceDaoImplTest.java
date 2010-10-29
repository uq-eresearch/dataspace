package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 29/10/2010
 * Time: 10:33:06 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ServiceDaoImplTest {

    @Test
    public void testAddingService() throws Exception {
        assertEquals(1, 1);
    }
}
