package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.NonProductionConstants;
import org.junit.Ignore;
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
import javax.naming.directory.SearchResult;

import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 25/02/2011
 * Time: 4:29:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class LDAPUtilTest {
    @Test
    @Ignore
    public void testSearchLDAPByEmail() throws Exception {
        String testEmail = "a.alabri@uq.edu.au";
        NamingEnumeration<SearchResult> namingEnumeration = searchLDAPByEmail(testEmail);
        Map<String, String> map = LDAPUtil.getAttributesAsMap(namingEnumeration);
        for (String s : map.keySet()) {
            System.out.println(s + ": " + map.get(s));
        }
        assertEquals("Email is not the same: ", testEmail, map.get("mail"));
    }

    private static NamingEnumeration<SearchResult> searchLDAPByEmail(String email) {
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
            SearchControls ctls = new SearchControls();
            ctls.setReturningObjFlag(true);
            ctls.setCountLimit(1);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            DirContext ctx = new InitialDirContext(env);
            NamingEnumeration<SearchResult> answer = ctx.search("ou=staff,ou=people,o=the university of queensland", "(mail=" + email + ")", ctls);
            return answer;
        } catch (NamingException e) {
            String message = "User not found in LDAP";
            System.err.println(message);
            return null;
        }
    }
}
