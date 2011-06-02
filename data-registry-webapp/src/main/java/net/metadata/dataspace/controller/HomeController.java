package net.metadata.dataspace.controller;

import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: alabri
 * Date: 2/06/11
 * Time: 11:09 AM
 */
public class HomeController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/index.jsp");

        List<Collection> recentPublishedCollections = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getRecentPublished(5);
        List<CollectionVersion> collectionVersions = new ArrayList<CollectionVersion>();
        for (Collection collection : recentPublishedCollections) {
            collectionVersions.add(collection.getPublished());
        }
        modelAndView.addObject("collections", collectionVersions);

        modelAndView.addObject("registryUri", RegistryApplication.getApplicationContext().getUriPrefix());
//        modelAndView.addObject("version", RegistryApplication.getApplicationContext().getVersion());
        return modelAndView;
    }
}
