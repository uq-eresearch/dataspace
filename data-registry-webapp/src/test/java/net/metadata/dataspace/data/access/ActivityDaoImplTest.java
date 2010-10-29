package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static net.sf.json.test.JSONAssert.assertEquals;

/**
 * Author: alabri
 * Date: 29/10/2010
 * Time: 10:32:48 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class ActivityDaoImplTest {

    @Test
    public void testAddingActivity() throws Exception {
        assertEquals(1, 1);
    }
}
