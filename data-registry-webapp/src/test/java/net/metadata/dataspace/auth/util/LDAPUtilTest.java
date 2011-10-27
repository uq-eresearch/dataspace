package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.atom.util.AdapterInputHelper;
import net.metadata.dataspace.data.model.record.Agent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: alabri
 * Date: 25/02/2011
 * Time: 4:29:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class LDAPUtilTest {

	@Autowired
	private AdapterInputHelper adapterInputHelper;

	@Test
    public void testSearchLDAPByEmail() throws Exception {
        String testEmail = "t.shyy@uq.edu.au";
        List<Agent> agents = adapterInputHelper.searchLDAPByEmail(testEmail);
        assertNotNull(agents);
        assertTrue(agents.size() > 0);
        Agent agent = agents.get(0);
        System.out.println(agent);
        assertTrue("Email is not the same: ", agent.getMBoxes().contains(testEmail));
    }

}
