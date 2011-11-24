package net.metadata.dataspace.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.User;
import net.metadata.dataspace.data.model.version.CollectionVersion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@Autowired
	private CollectionDao collectionDao;

	@RequestMapping("/")
    public ModelAndView indexHandler(HttpSession session)
	{
        ModelAndView modelAndView = new ModelAndView("index");
        List<Collection> recentPublishedCollections = collectionDao.getRecentPublished(5);
        List<CollectionVersion> collectionVersions = new LinkedList<CollectionVersion>();
        for (Collection collection : recentPublishedCollections) {
            collectionVersions.add(collection.getPublished());
        }
        modelAndView.addObject(collectionVersions);
        populateHeaderFooterObjects(modelAndView, session);
        return modelAndView;
    }

	@RequestMapping("/about")
    public ModelAndView aboutHandler(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("about");
        populateHeaderFooterObjects(modelAndView, session);
        return modelAndView;

	}

	@RequestMapping("/search")
    public ModelAndView searchHandler(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("search");
        populateHeaderFooterObjects(modelAndView, session);
        return modelAndView;
	}

	private void populateHeaderFooterObjects(
			ModelAndView modelAndView, HttpSession session)
	{
        modelAndView.addObject("registryTitle", RegistryApplication.getApplicationContext().getRegistryTitle());
        modelAndView.addObject("registryVersion", RegistryApplication.getApplicationContext().getVersion());
        User currentUser = (User) session.getAttribute(Constants.SESSION_ATTRIBUTE_CURRENT_USER);
        modelAndView.addObject("currentUser", currentUser);
	}


}
