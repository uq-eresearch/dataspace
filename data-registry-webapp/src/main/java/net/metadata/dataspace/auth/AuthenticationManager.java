package net.metadata.dataspace.auth;

import javax.naming.NamingException;

import net.metadata.dataspace.data.model.record.User;
import org.apache.abdera.protocol.server.RequestContext;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:00:18 PM
 */
public interface AuthenticationManager {

    /**
     * Returns the currently logged in user.
     *
     * @param request The HTTP request for which the user is to be found. Not null.
     * @return The user logged in for this HTTP conversation or null if no one
     *         is logged in.
     */
    User getCurrentUser(RequestContext request);

    /**
     * Attempts to authenticate the username and password against the default
     * users list.
     *
     * @param userName
     * @param password
     * @return User if successful, null otherwise
     */
	User authenticateDefaultUser(String userName, String password);


    /**
     * Attempts to authenticate the username and password against LDAP.
     *
     * @param userName
     * @param password
     * @return User if successful, null otherwise
     * @throws InvalidCredentialsException
     * @throws NamingException on LDAP failure
     */
	User authenticateLdapUser(String userName, String password)
			throws NamingException;

}
