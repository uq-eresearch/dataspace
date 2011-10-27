package net.metadata.dataspace.controller;

import java.util.LinkedList;
import java.util.List;

import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@Autowired
	private CollectionDao collectionDao;

	@RequestMapping("/")
    public ModelAndView indexHandler() {
        ModelAndView modelAndView = new ModelAndView("index");
        List<Collection> recentPublishedCollections = collectionDao.getRecentPublished(5);
        List<CollectionVersion> collectionVersions = new LinkedList<CollectionVersion>();
        for (Collection collection : recentPublishedCollections) {
            collectionVersions.add(collection.getPublished());
        }
        modelAndView.addObject(collectionVersions);
        modelAndView.addObject("registryTitle", RegistryApplication.getApplicationContext().getRegistryTitle());
        return modelAndView;
    }
}
