package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Context;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
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

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:36:01 PM
 */
@Transactional
public class EntityCreatorImpl implements EntityCreator {

    private AgentSequencer agentSequencer;
    private CollectionSequencer collectionSequencer;
    private SubjectSequencer subjectSequencer;
    private ServiceSequencer serviceSequencer;
    private ActivitySequencer activitySequencer;
    private PublicationSequencer publicationSequencer;
    private SourceSequencer sourceSequencer;
    private FullNameSequencer fullNameSequencer;

    private final Logger logger = Logger.getLogger(getClass());

    public EntityCreatorImpl() {
    }

    @SuppressWarnings("unchecked")
	@Override
    public <R extends Record<?>> R getNextRecord(Class<R> clazz) {
        if (clazz.equals(Activity.class)) {
            return (R) getNextActivity();
        } else if (clazz.equals(Collection.class)) {
            return (R) getNextCollection();
        } else if (clazz.equals(Agent.class)) {
            return (R) getNextAgent();
        } else if (clazz.equals(Service.class)) {
            return (R) getNextService();
        }
        return null;
    }

    @Override
    public Context getNextResource(Class<?> clazz) {
        if (clazz.equals(Source.class)) {
            return getNextSource();
        } else if (clazz.equals(Publication.class)) {
            return getNextPublication();
        } else if (clazz.equals(Subject.class)) {
            return getNextSubject();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <R extends Record<?>, V extends Version<?>> V getNextVersion(R record) {
        V version = null;
        if (record instanceof Activity) {
            version = (V) getActivityVersion((Activity) record);
        } else if (record instanceof Collection) {
            version = (V) getCollectionVersion((Collection) record);
        } else if (record instanceof Agent) {
            version = (V) getAgentVersion((Agent) record);
        } else if (record instanceof Service) {
            version = (V) getServiceVersion((Service) record);
        }
        if (version.getAtomicNumber() == null) {
        	logger.warn("Version atomic number is null, but should not be.");
        }
        return version;
    }

	protected ServiceVersion getServiceVersion(Service record) {
		ServiceVersion version = new ServiceVersion();
		version.setParent(record);
		record.addVersion(version);
		return version;
	}

	protected AgentVersion getAgentVersion(Agent record) {
		AgentVersion version = new AgentVersion();
		version.setParent(record);
		record.addVersion(version);
		return version;
	}

	protected CollectionVersion getCollectionVersion(Collection record) {
		CollectionVersion version = new CollectionVersion();
		version.setParent(record);
		record.addVersion(version);
		return version;
	}

	protected ActivityVersion getActivityVersion(Activity record) {
		ActivityVersion version = new ActivityVersion();
		version.setParent(record);
		record.addVersion(version);
		return version;
	}


    @Override
    public Publication getNextPublication() {
        Publication publication = new Publication();
        publication.setAtomicNumber(publicationSequencer.next());
        return publication;
    }

    @Override
    public FullName getFullName() {
        FullName fullName = new FullName();
        fullName.setAtomicNumber(fullNameSequencer.next());
        return fullName;
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

    private AbstractRecordEntity<ActivityVersion> getNextActivity() {
        AbstractRecordEntity<ActivityVersion> activity = new Activity();
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


    public void setFullNameSequencer(FullNameSequencer fullNameSequencer) {
        this.fullNameSequencer = fullNameSequencer;
    }

    public FullNameSequencer getFullNameSequencer() {
        return fullNameSequencer;
    }
}
