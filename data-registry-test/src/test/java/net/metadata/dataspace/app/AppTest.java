package net.metadata.dataspace.app;

import net.metadata.dataspace.atom.util.ClientHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author: alabri
 * Date: 24/11/2010
 * Time: 10:21:04 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class AppTest {

	@Test
    public void testPublicFrontPage() throws Exception {
        //create a client
		HttpClient client = new HttpClient();
        GetMethod result;
        String[] locations = { "", "browse", 
        		Constants.PATH_FOR_ACTIVITIES,  
        		Constants.PATH_FOR_AGENTS,
        		Constants.PATH_FOR_COLLECTIONS,
        		Constants.PATH_FOR_SERVICES };
        for (int i = 0; i < locations.length; i++) {
        	result = ClientHelper.getEntry(client, Constants.URL_PREFIX+locations[i], "text/html");
        	assertEquals(result.getURI()+" should return 200 OK", 200, result.getStatusCode());
        	assertTrue(result.getURI()+" should return not be zero length", 
        			0 < result.getResponseBody().length);
        }
    }
	
    @Test
    public void testCorrectLoginLogout() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //correct username and password
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);
    }

    @Test
    public void testIncorrectLogin() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //incorrect password
        int status = ClientHelper.login(client, "uqaalabr", "wrong-password");
        assertEquals("Incorrect password test, wrong status code", 400, status);

        //incorrect username and password
        status = ClientHelper.login(client, "wrong-username", "wrong-password");
        assertEquals("Incorrect username and password test, wrong status code", 400, status);
    }

    @Test
    public void testGetServiceDescription() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, Constants.URL_PREFIX + "registry.atomsvc", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get registry service description", 200, getMethod.getStatusCode());
    }

}
