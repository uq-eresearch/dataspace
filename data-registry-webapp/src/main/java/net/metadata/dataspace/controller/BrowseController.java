package net.metadata.dataspace.controller;

import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: alabri
 * Date: 17/03/11
 * Time: 10:44 AM
 */
public class BrowseController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("browse");

        List<Activity> recentPublishedActivities = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao().getRecentPublished(5);
        List<ActivityVersion> activityVersions = new ArrayList<ActivityVersion>();
        for (Activity activity : recentPublishedActivities) {
            activityVersions.add(activity.getPublished());
        }
        modelAndView.addObject("activityies", activityVersions);

        List<Collection> recentPublishedCollections = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getRecentPublished(5);
        List<CollectionVersion> collectionVersions = new ArrayList<CollectionVersion>();
        for (Collection collection : recentPublishedCollections) {
            collectionVersions.add(collection.getPublished());
        }
        modelAndView.addObject("collections", collectionVersions);

        List<Agent> recentPublishedAgents = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao().getRecentPublished(5);
        List<AgentVersion> agentVersions = new ArrayList<AgentVersion>();
        for (Agent agent : recentPublishedAgents) {
            agentVersions.add(agent.getPublished());
        }
        modelAndView.addObject("agents", agentVersions);

        List<Service> recentPublishedServices = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao().getRecentPublished(5);
        List<ServiceVersion> serviceVersions = new ArrayList<ServiceVersion>();
        for (Service service : recentPublishedServices) {
            serviceVersions.add(service.getPublished());
        }
        modelAndView.addObject("services", serviceVersions);

        modelAndView.addObject("registryUri", RegistryApplication.getApplicationContext().getUriPrefix());
//        modelAndView.addObject("version", RegistryApplication.getApplicationContext().getVersion());
        return modelAndView;
    }
}
