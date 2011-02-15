package net.metadata.dataspace.auth;

import net.metadata.dataspace.data.model.record.User;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

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
     * Logs the given user in for the HTTP conversation.
     *
     * @param request The request of the HTTP conversation. Not null.
     */
    ResponseContext login(RequestContext request);

    /**
     * Removes any user from the HTTP conversation.
     * <p/>
     * If no user is logged in this operation should do nothing.
     *
     * @param request The request of the HTTP conversation. Not null.
     */
    ResponseContext logout(RequestContext request);
}
