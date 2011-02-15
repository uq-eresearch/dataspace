package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Context;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import net.metadata.dataspace.data.sequencer.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:36:01 PM
 */
public class EntityCreatorImpl implements EntityCreator {

    private AgentSequencer agentSequencer;
    private CollectionSequencer collectionSequencer;
    private SubjectSequencer subjectSequencer;
    private ServiceSequencer serviceSequencer;
    private ActivitySequencer activitySequencer;
    private PublicationSequencer publicationSequencer;
    private SourceSequencer sourceSequencer;

    public EntityCreatorImpl() {
    }

    @Override
    public Record getNextRecord(Class clazz) {
        if (clazz.equals(Activity.class)) {
            return getNextActivity();
        } else if (clazz.equals(Collection.class)) {
            return getNextCollection();
        } else if (clazz.equals(Agent.class)) {
            return getNextAgent();
        } else if (clazz.equals(Service.class)) {
            return getNextService();
        }
        return null;
    }

    @Override
    public Context getNextResource(Class clazz) {
        if (clazz.equals(Source.class)) {
            return getNextSource();
        } else if (clazz.equals(Publication.class)) {
            return getNextPublication();
        } else if (clazz.equals(Subject.class)) {
            return getNextSubject();
        }
        return null;
    }

    @Override
    public Version getNextVersion(Record record) {
        Version version = null;
        if (record instanceof Activity) {
            version = new ActivityVersion();
        } else if (record instanceof Collection) {
            version = new CollectionVersion();
        } else if (record instanceof Agent) {
            version = new AgentVersion();
        } else if (record instanceof Service) {
            version = new ServiceVersion();
        }
        AtomicInteger atomicInteger = new AtomicInteger(record.getVersions().size());
        version.setAtomicNumber(atomicInteger.incrementAndGet());
        return version;
    }

    @Override
    public Publication getNextPublication() {
        Publication publication = new Publication();
        publication.setAtomicNumber(publicationSequencer.next());
        return publication;
    }

    @Override
    public Source getNextSource() {
        Source source = new Source();
        source.setAtomicNumber(sourceSequencer.next());
        return source;
    }

    @Override
    public Subject getNextSubject() {
        Subject subject = new Subject();
        subject.setAtomicNumber(subjectSequencer.next());
        return subject;
    }

    private Agent getNextAgent() {
        Agent agent = new Agent();
        agent.setAtomicNumber(agentSequencer.next());
        return agent;
    }

    private Collection getNextCollection() {
        Collection collection = new Collection();
        collection.setAtomicNumber(collectionSequencer.next());
        return collection;
    }

    private Service getNextService() {
        Service service = new Service();
        service.setAtomicNumber(serviceSequencer.next());
        return service;
    }

    private Activity getNextActivity() {
        Activity activity = new Activity();
        activity.setAtomicNumber(activitySequencer.next());
        return activity;
    }

    public void setAgentSequencer(AgentSequencer agentSequencer) {
        this.agentSequencer = agentSequencer;
    }

    public AgentSequencer getAgentSequencer() {
        return agentSequencer;
    }

    public void setCollectionSequencer(CollectionSequencer collectionSequencer) {
        this.collectionSequencer = collectionSequencer;
    }

    public CollectionSequencer getCollectionSequencer() {
        return collectionSequencer;
    }

    public void setSubjectSequencer(SubjectSequencer subjectSequencer) {
        this.subjectSequencer = subjectSequencer;
    }

    public SubjectSequencer getSubjectSequencer() {
        return subjectSequencer;
    }

    public void setServiceSequencer(ServiceSequencer serviceSequencer) {
        this.serviceSequencer = serviceSequencer;
    }

    public ServiceSequencer getServiceSequencer() {
        return serviceSequencer;
    }

    public void setActivitySequencer(ActivitySequencer activitySequencer) {
        this.activitySequencer = activitySequencer;
    }

    public ActivitySequencer getActivitySequencer() {
        return activitySequencer;
    }

    public void setPublicationSequencer(PublicationSequencer publicationSequencer) {
        this.publicationSequencer = publicationSequencer;
    }

    public PublicationSequencer getPublicationSequencer() {
        return publicationSequencer;
    }

    public void setSourceSequencer(SourceSequencer sourceSequencer) {
        this.sourceSequencer = sourceSequencer;
    }

    public SourceSequencer getSourceSequencer() {
        return sourceSequencer;
    }
}
