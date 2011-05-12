package net.metadata.dataspace.controller;

import net.metadata.dataspace.util.ANZSRCLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: alabri
 * Date: 12/05/11
 * Time: 2:41 PM
 */
public class ANZSRCCodesController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("anzsrc");
        boolean result = ANZSRCLoader.loadANZSRCCodes();
        if (result) {
            modelAndView.addObject("result", "Done!");
        } else {
            modelAndView.addObject("result", "Problem injecting ANZSRC codes");
        }
        return modelAndView;
    }

}
