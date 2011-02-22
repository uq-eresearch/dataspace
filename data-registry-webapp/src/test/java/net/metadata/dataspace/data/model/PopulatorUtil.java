package net.metadata.dataspace.data.model;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.ActivityType;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.types.CollectionType;
import net.metadata.dataspace.data.model.types.ServiceType;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import java.util.*;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:22:08 AM
 */
public class PopulatorUtil {
    private static EntityCreator entityCreator;
    private static DaoManager daoManager;

    public static Subject getSubject() throws Exception {
        Subject subject = entityCreator.getNextSubject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = uuid.toString();
        subject.setTerm(vocabUriString);
        subject.setDefinedBy("Test Subject");
        return subject;
    }

    public static Source getSource() throws Exception {
        Source source = entityCreator.getNextSource();
        source.setSourceURI("http://uq.edu.au");
        source.setTitle("The university of queensland");
        return source;
    }

    public static CollectionVersion getCollectionVersion(Record collection) throws Exception {
        CollectionVersion collectionVersion = (CollectionVersion) entityCreator.getNextVersion(collection);
        collectionVersion.setParent(collection);

        collectionVersion.setType(CollectionType.COLLECTION);
        collectionVersion.setTitle("Test Collection");
        collectionVersion.setDescription("Test Collection Content");
        collectionVersion.setPage("http://test.location.com.au/collection");
        collectionVersion.setRights("Test rights text");
        collectionVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Collection Author");
        collectionVersion.setAuthors(authors);
        return collectionVersion;
    }

    public static AgentVersion getAgentVersion(Record agent) throws Exception {
        AgentVersion agentVersion = (AgentVersion) entityCreator.getNextVersion(agent);
        agentVersion.setParent(agent);
        agentVersion.setTitle("Test Agent Title");
        agentVersion.setMbox("email@company.com");
        agentVersion.setDescription("Test Agent Content");
        agentVersion.setType(AgentType.PERSON);
        agentVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Agent Author");
        agentVersion.setAuthors(authors);
        agent.setUpdated(new Date());
        return agentVersion;
    }

    public static ServiceVersion getServiceVersion(Record service) {
        ServiceVersion serviceVersion = (ServiceVersion) entityCreator.getNextVersion(service);
        serviceVersion.setParent(service);
        serviceVersion.setTitle("Test Service Title");
        serviceVersion.setDescription("Test Service Content");
        serviceVersion.setType(ServiceType.SYNDICATE);
        serviceVersion.setUpdated(new Date());
        serviceVersion.setPage("http://test.location.com.au/collection");
        Set<String> authors = new HashSet<String>();
        authors.add("Test Service Author");
        serviceVersion.setAuthors(authors);
        return serviceVersion;
    }

    public static ActivityVersion getActivityVersion(Record activity) {
        ActivityVersion activityVersion = (ActivityVersion) entityCreator.getNextVersion(activity);
        activityVersion.setParent(activity);
        activityVersion.setTitle("Test Activity Title");
        activityVersion.setDescription("Test Activity Content");
        activityVersion.setType(ActivityType.PROJECT);
        activityVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Activity Author");
        activityVersion.setAuthors(authors);
        return activityVersion;
    }

    public static void cleanup() {
        AgentDao agentDao = daoManager.getAgentDao();
        AgentVersionDao agentVersionDao = daoManager.getAgentVersionDao();
        deleteEntity(agentDao, agentVersionDao);

        CollectionDao collectionDao = daoManager.getCollectionDao();
        CollectionVersionDao collectionVersionDao = daoManager.getCollectionVersionDao();
        deleteEntity(collectionDao, collectionVersionDao);

        ActivityDao activityDao = daoManager.getActivityDao();
        ActivityVersionDao activityVersionDao = daoManager.getActivityVersionDao();
        deleteEntity(activityDao, activityVersionDao);

        ServiceDao serviceDao = daoManager.getServiceDao();
        ServiceVersionDao serviceVersionDao = daoManager.getServiceVersionDao();
        deleteEntity(serviceDao, serviceVersionDao);
    }

    private static void deleteEntity(Dao parentDao, Dao versionDao) {
        List<Record> recordList = parentDao.getAll();
        for (Record record : recordList) {
            SortedSet<Version> versions = record.getVersions();
            for (Version version : versions) {
                if (parentDao instanceof AgentDao && versionDao instanceof AgentVersionDao) {
                    AgentVersion ver = (AgentVersion) version;
                    Set<Activity> activitySet = ver.getCurrentProjects();
                    for (Activity activity : activitySet) {
                        SortedSet<ActivityVersion> activityVersions = activity.getVersions();
                        for (ActivityVersion activityVersion : activityVersions) {
                            activityVersion.getHasParticipants().remove(record);
                        }
                    }
                    activitySet.removeAll(activitySet);
                    Set<Collection> collectionSet = ver.getIsManagerOf();
                    for (Collection collection : collectionSet) {
                        SortedSet<CollectionVersion> collectionVersions = collection.getVersions();
                        for (CollectionVersion collectionVersion : collectionVersions) {
                            collectionVersion.getCreators().remove(record);
                        }
                        for (CollectionVersion collectionVersion : collectionVersions) {
                            collectionVersion.getPublishers().remove(record);
                        }
                    }
                    collectionSet.removeAll(collectionSet);
                    ver.getSubjects().removeAll(ver.getSubjects());

                }
                if (parentDao instanceof CollectionDao && versionDao instanceof CollectionVersionDao) {
                    CollectionVersion ver = (CollectionVersion) version;
                    Set<Activity> activities = ver.getOutputOf();
                    for (Activity activity : activities) {
                        SortedSet<ActivityVersion> activityVersions = activity.getVersions();
                        for (ActivityVersion activityVersion : activityVersions) {
                            activityVersion.getHasOutput().remove(record);
                        }
                    }
                    activities.removeAll(activities);
                    Set<Service> services = ver.getAccessedVia();
                    for (Service service : services) {
                        SortedSet<ServiceVersion> serviceVersions = service.getVersions();
                        for (ServiceVersion serviceVersion : serviceVersions) {
                            serviceVersion.getSupportedBy().remove(record);
                        }
                    }
                    services.removeAll(services);
                    Set<Agent> agents = ver.getCreators();
                    for (Agent agent : agents) {
                        SortedSet<AgentVersion> agentVersions = agent.getVersions();
                        for (AgentVersion agentVersion : agentVersions) {
                            agentVersion.getIsManagerOf().remove(record);
                        }
                        for (AgentVersion agentVersion : agentVersions) {
                            agentVersion.getMade().remove(record);
                        }
                    }
                    agents.removeAll(agents);
                    ver.getSubjects().removeAll(ver.getSubjects());
                }
                if (parentDao instanceof ActivityDao && versionDao instanceof ActivityVersionDao) {
                    ActivityVersion ver = (ActivityVersion) version;
                    Set<Collection> collections = ver.getHasOutput();
                    for (Collection collection : collections) {
                        SortedSet<CollectionVersion> collectionVersions = collection.getVersions();
                        for (CollectionVersion collectionVersion : collectionVersions) {
                            collectionVersion.getOutputOf().remove(record);
                        }
                    }
                    collections.removeAll(collections);
                    Set<Agent> agents = ver.getHasParticipants();
                    for (Agent agent : agents) {
                        SortedSet<AgentVersion> agentVersions = agent.getVersions();
                        for (AgentVersion agentVersion : agentVersions) {
                            agentVersion.getCurrentProjects().remove(record);
                        }
                    }
                    agents.removeAll(agents);
                }
                if (parentDao instanceof ServiceDao && versionDao instanceof ServiceVersionDao) {
                    ServiceVersion ver = (ServiceVersion) version;
                    Set<Collection> collections = ver.getSupportedBy();
                    for (Collection collection : collections) {
                        SortedSet<CollectionVersion> collectionVersions = collection.getVersions();
                        for (CollectionVersion collectionVersion : collectionVersions) {
                            collectionVersion.getAccessedVia().remove(record);
                        }
                    }
                    collections.removeAll(collections);
                }
                version.getAuthors().removeAll(version.getAuthors());
                record.getVersions().remove(version);
                versionDao.delete(version);
            }
            parentDao.delete(record);
        }
    }

    public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

    public void setDaoManager(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }
}
