package net.metadata.dataspace.controller;

import net.metadata.dataspace.controller.command.SearchCommand;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: alabri
 * Date: 29/03/11
 * Time: 8:22 AM
 */
public class SearchController extends SimpleFormController {

    public SearchController() {
        super();
        setCommandClass(SearchCommand.class);
        setFormView("search");
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException error) throws Exception {
        Assert.notNull(command);
        if (command != null) {
            String keyword = request.getParameter("search-field");
            Assert.notNull(keyword, "search-field has not been set.");
            ModelAndView searchModelView = new ModelAndView("search");
            searchModelView.addObject("keyword", keyword);
            return searchModelView;
        }
        return null;
    }

}
