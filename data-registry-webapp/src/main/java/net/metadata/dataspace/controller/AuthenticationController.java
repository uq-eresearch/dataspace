package net.metadata.dataspace.controller;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.model.record.User;

import org.apache.abdera.protocol.server.ResponseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthenticationController {

    @Autowired
    protected AuthenticationManager authenticationManager;

    /**
     * Logs the given user in for the HTTP conversation.
     *
     * @param request The request of the HTTP conversation. Not null.
     * @throws IOException
     */
	@RequestMapping("/login")
    public void login(HttpServletRequest request,
    		@RequestParam("username") String userName,
    		@RequestParam("password") String password,
    		HttpServletResponse response) throws IOException
	{
		// If HEAD request, just send 200 OK
        if (request.getMethod().equals("HEAD")) {
            response.setStatus(200);
        	return;
        }
        if (userName == null || password == null) {
        	response.sendError(400, "Username and password missing");
            return;
        }
    	User user = authenticationManager.authenticateDefaultUser(userName, password);
    	try {
	    	if (user == null) {
	        	user = authenticationManager.authenticateLdapUser(userName, password);
	    	}
    	} catch (NamingException e) {
        	response.sendError(400, e.getMessage());
        	return;
    	}
    	if (user == null) {
    		response.sendError(400, "Bad username or password");
    		return;
    	}
        request.getSession().setAttribute(
        		Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
        response.setStatus(200);
    }

	/**
     * Removes any user from the HTTP conversation.
     * <p/>
     * If no user is logged in this operation should do nothing.
     *
     * @param session The session of the HTTP conversation. Not null.
     */
	@RequestMapping("/logout")
    public ModelAndView logoutHandler(HttpSession session) {
		session.setAttribute(Constants.SESSION_ATTRIBUTE_CURRENT_USER, null);
		return null;
	}


}
