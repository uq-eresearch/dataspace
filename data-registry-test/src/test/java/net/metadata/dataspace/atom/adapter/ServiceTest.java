package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.ClientHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 * Author: alabri
 * Date: 05/11/2010
 * Time: 5:32:54 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Constants.TEST_CONTEXT)
public class ServiceTest {

    @Test
    public void testServiceCRUD() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-service.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();
        //Get entry
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get entry after post", 200, getMethod.getStatusCode());
        //get first version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get first version of entry after post", 200, getMethod.getStatusCode());
        //get working copy
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/working-copy", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get working copy after post", 200, getMethod.getStatusCode());
        //Edit Entry
        fileName = "/files/put/update-service.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not edit entry", 200, putMethod.getStatusCode());
        //get second version
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/2", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get second version of entry after edit", 200, getMethod.getStatusCode());
        //Get version history
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not get working copy after post", 200, getMethod.getStatusCode());
        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Could not delete entry", 200, deleteMethod.getStatusCode());
        //check that entry is deleted
        getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Entry should be GONE", 410, getMethod.getStatusCode());
    }

    @Test
    public void testServiceUnauthorized() throws Exception {
        //create a client
        HttpClient client = new HttpClient();

        //post without authentication
        String fileName = "/files/post/new-service.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Posting without authenticating, Wrong status code", 401, postMethod.getStatusCode());

        //login
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);

        //post with authentication
        postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating, Wrong status code", 404, getMethod.getStatusCode());

        //get first version without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/1", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get first version without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get working copy without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/working-copy", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get working copy without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //get version history without authenticating
        getMethod = ClientHelper.getEntry(client, newEntryLocation + "/version-history", Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get version history without authenticating, Wrong status code", 401, getMethod.getStatusCode());

        //Edit without authenticating
        fileName = "/files/put/update-service.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Editing without authenticating, Wrong status code", 401, putMethod.getStatusCode());

        //Delete Entry
        DeleteMethod deleteMethod = ClientHelper.deleteEntry(client, newEntryLocation);
        assertEquals("Deleting without authenticating, Wrong status code", 401, deleteMethod.getStatusCode());
    }

    @Test
    public void testServicePublishing() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-service.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-service.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Get without authenticating should now return OK", 200, getMethod.getStatusCode());
    }

    @Test
    public void testServiceFeed() throws Exception {
        //create a client
        HttpClient client = new HttpClient();
        //authenticate
        int status = ClientHelper.login(client, Constants.USERNAME, Constants.PASSWORD);
        assertEquals("Could not authenticate", 200, status);
        //Post Entry
        String fileName = "/files/post/new-service.xml";
        PostMethod postMethod = ClientHelper.postEntry(client, fileName, Constants.PATH_FOR_SERVICES);
        assertEquals("Could not post entry", 201, postMethod.getStatusCode());
        String newEntryLocation = postMethod.getResponseHeader("Location").getValue();

        //publish entry
        fileName = "/files/put/published-service.xml";
        PutMethod putMethod = ClientHelper.putEntry(client, fileName, newEntryLocation, Constants.ATOM_ENTRY_MIMETYPE);
        assertEquals("Could not publish entry", 200, putMethod.getStatusCode());

        //logout
        status = ClientHelper.logout(client);
        assertEquals("Could not logout", 200, status);

        String feedUrl = Constants.URL_PREFIX + Constants.PATH_FOR_SERVICES;
        //get without authenticating
        GetMethod getMethod = ClientHelper.getEntry(client, feedUrl, Constants.ATOM_FEED_MIMETYPE);
        assertEquals("Could not get feed", 200, getMethod.getStatusCode());
    }
}

