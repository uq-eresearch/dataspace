package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import java.util.Hashtable;
import java.util.Map;

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
        String testEmail = "uqaalabr@uq.edu.au";
        NamingEnumeration namingEnumeration = searchLDAPByEmail(testEmail);
        Map<String, String> map = LDAPUtil.getAttributesAsMap(namingEnumeration);
        for (String s : map.keySet()) {
            System.out.println(s + ": " + map.get(s));
        }
//        assertEquals("Email is not the same: ", testEmail, map.get("uqmail"));
    }

    private static NamingEnumeration searchLDAPByEmail(String email) {
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
            SearchControls ctls = new SearchControls();
            ctls.setReturningObjFlag(true);
            ctls.setCountLimit(1);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            DirContext ctx = new InitialDirContext(env);
            NamingEnumeration answer = ctx.search("ou=staff,ou=people,o=the university of queensland", "(uqmail=" + email + ")", ctls);
            return answer;
        } catch (NamingException e) {
            String message = "User not found in LDAP";
            System.err.println(message);
            return null;
        }
    }
}
