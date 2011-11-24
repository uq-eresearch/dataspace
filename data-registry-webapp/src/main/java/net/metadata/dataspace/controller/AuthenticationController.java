package net.metadata.dataspace.controller;

import javax.servlet.http.HttpSession;

import net.metadata.dataspace.app.Constants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthenticationController {

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
