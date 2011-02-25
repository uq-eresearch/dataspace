package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.NamingEnumeration;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 25/02/2011
 * Time: 4:29:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class LDAPUtilTest {
    @Test
    public void testSearchLDAPByEmail() throws Exception {
        String testEmail = "a.alabri@uq.edu.au";
        NamingEnumeration namingEnumeration = LDAPUtil.searchLDAPByEmail(testEmail);
        Map<String, String> map = LDAPUtil.getAttributesAsMap(namingEnumeration);
        for (String s : map.keySet()) {
            System.out.println(s + ": " + map.get(s));
        }
        assertEquals("Email is not the same: ", testEmail, map.get("mail"));
    }
}
