package net.metadata.dataspace.controller;

import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: alabri
 * Date: 29/03/11
 * Time: 9:43 AM
 */
public class LookupController extends SimpleFormController {

    public LookupController() {
        super();
        setFormView("lookup");
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException error) throws Exception {
        Assert.notNull(command);
        if (command != null) {
            String keyword = request.getParameter("keyword");
            Assert.notNull(keyword, "keyword has not been set.");
//            ModelAndView searchModelView = new ModelAndView("lookup");
//            if (keyword != null && !keyword.isEmpty()) {
//                List<String> result = new ArrayList<String>();
//                result.add("Result 1");
//                result.add("Result 2");
//                result.add("Result 3");
//                searchModelView.addObject("result", result);
//            }
//            return searchModelView;
        }
        return null;
    }
}
