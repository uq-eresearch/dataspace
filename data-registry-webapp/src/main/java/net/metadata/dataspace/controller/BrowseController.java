package net.metadata.dataspace.controller;

import net.metadata.dataspace.app.RegistryApplication;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: alabri
 * Date: 17/03/11
 * Time: 10:44 AM
 */
public class BrowseController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("browse");
        modelAndView.addObject("version", RegistryApplication.getApplicationContext().getVersion());
        return modelAndView;
    }
}
