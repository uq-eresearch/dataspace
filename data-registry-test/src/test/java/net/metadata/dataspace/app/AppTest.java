package net.metadata.dataspace.app;

import net.metadata.dataspace.atom.util.TestHelper;
import net.sourceforge.jwebunit.junit.WebTestCase;
import org.apache.commons.httpclient.HttpClient;

/**
 * Author: alabri
 * Date: 24/11/2010
 * Time: 10:21:04 AM
 */
public class AppTest extends WebTestCase {

    public void testCorrectLoginLogout() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //correct username and password
        int status = TestHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //logout
        status = TestHelper.logout(client);
        assertEquals("Could not logout", 200, status);
    }

    public void testIncorrectLogin() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //incorrect password
        int status = TestHelper.login(client, "uqaalabr", "wrong-password");
        assertEquals("Incorrect password test, wrong status code", 400, status);

        //incorrect username and password
        status = TestHelper.login(client, "wrong-username", "wrong-password");
        assertEquals("Incorrect username and password test, wrong status code", 400, status);
    }

}
