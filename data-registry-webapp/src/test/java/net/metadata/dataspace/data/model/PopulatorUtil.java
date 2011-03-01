package net.metadata.dataspace.data.model;

import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.types.ActivityType;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.types.CollectionType;
import net.metadata.dataspace.data.model.types.ServiceType;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

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
        source.setSourceURI("http://uq.edu.au" + UUID.randomUUID().toString());
        source.setTitle("The university of queensland");
        return source;
    }

    public static Publication getPublication() {
        Publication publication = entityCreator.getNextPublication();
        publication.setPublicationURI("http://uq.edu.au/publication");
        publication.setTitle("Test publication");
        return publication;
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
        return serviceVersion;
    }

    public static ActivityVersion getActivityVersion(Record activity) {
        ActivityVersion activityVersion = (ActivityVersion) entityCreator.getNextVersion(activity);
        activityVersion.setParent(activity);
        activityVersion.setTitle("Test Activity Title");
        activityVersion.setDescription("Test Activity Content");
        activityVersion.setType(ActivityType.PROJECT);
        activityVersion.setUpdated(new Date());
        return activityVersion;
    }

    public static void cleanup() {
        EntityManager entityManager = daoManager.getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query dropQuery = entityManager.createNativeQuery("DROP SCHEMA registry if exists; CREATE SCHEMA registry;");
        dropQuery.executeUpdate();
        transaction.commit();
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
