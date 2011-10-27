package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterInputHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.model.record.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Author: alabri
 * Date: 25/02/2011
 * Time: 4:29:31 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class LDAPUtilTest {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AdapterInputHelper adapterInputHelper;

	@Test
    public void testSearchLDAPByEmail() throws Exception {
        String testEmail = "t.shyy@uq.edu.au";
        NamingEnumeration<SearchResult> namingEnumeration =
        		adapterInputHelper.searchLDAPByEmail(testEmail, getTestUser(testEmail));
        assertNotNull(namingEnumeration);
        Map<String, String> map = adapterInputHelper.getAttributesAsMap(namingEnumeration);
        for (String s : map.keySet()) {
            System.out.println(s + ": " + map.get(s));
        }
        assertEquals("Email is not the same: ", testEmail, map.get("mail"));
    }

	private User getTestUser(String email) {
        User testUser = new User();
        testUser.setEmail(email);
        authenticationManager.setDirContext(getTestDirContext(), testUser);
        return testUser;
	}

    private DirContext getTestDirContext() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.uq.edu.au");
        SearchControls ctls = new SearchControls();
        ctls.setReturningObjFlag(true);
        ctls.setCountLimit(1);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
			return new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
			fail("Unable to initialize DirContext");
			return null;
		}
    }
}
