package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import net.metadata.dataspace.data.sequencer.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:36:01 PM
 */
public class EntityCreatorImpl implements EntityCreator {

    private PartyAtomicSequencer partyAtomicSequencer;
    private CollectionAtomicSequencer collectionAtomicSequencer;
    private SubjectAtomicSequencer subjectAtomicSequencer;
    private ServiceAtomicSequencer serviceAtomicSequencer;
    private ActivityAtomicSequencer activityAtomicSequencer;

    public EntityCreatorImpl() {
    }

    @Override
    public Record getNextRecord(Class clazz) {
        if (clazz.equals(Activity.class)) {
            getNextActivity();
        } else if (clazz.equals(Collection.class)) {
            return getNextCollection();
        } else if (clazz.equals(Party.class)) {
            return getNextParty();
        } else if (clazz.equals(Service.class)) {
            return getNextService();
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
        } else if (record instanceof Party) {
            version = new PartyVersion();
        } else if (record instanceof Service) {
            version = new ServiceVersion();
        }
        AtomicInteger atomicInteger = new AtomicInteger(record.getVersions().size());
        version.setAtomicNumber(atomicInteger.incrementAndGet());
        return version;
    }


    @Override
    public Subject getNextSubject() {
        Subject subject = new Subject();
        subject.setAtomicNumber(subjectAtomicSequencer.next());
        return subject;
    }

    private Party getNextParty() {
        Party party = new Party();
        party.setAtomicNumber(partyAtomicSequencer.next());
        return party;
    }

    private Collection getNextCollection() {
        Collection collection = new Collection();
        collection.setAtomicNumber(collectionAtomicSequencer.next());
        return collection;
    }

    private Service getNextService() {
        Service service = new Service();
        service.setAtomicNumber(serviceAtomicSequencer.next());
        return service;
    }

    private Activity getNextActivity() {
        Activity activity = new Activity();
        activity.setAtomicNumber(activityAtomicSequencer.next());
        return activity;
    }

    public void setPartyAtomicSequencer(PartyAtomicSequencer partyAtomicSequencer) {
        this.partyAtomicSequencer = partyAtomicSequencer;
    }

    public PartyAtomicSequencer getPartyAtomicSequencer() {
        return partyAtomicSequencer;
    }

    public void setCollectionAtomicSequencer(CollectionAtomicSequencer collectionAtomicSequencer) {
        this.collectionAtomicSequencer = collectionAtomicSequencer;
    }

    public CollectionAtomicSequencer getCollectionAtomicSequencer() {
        return collectionAtomicSequencer;
    }

    public void setSubjectAtomicSequencer(SubjectAtomicSequencer subjectAtomicSequencer) {
        this.subjectAtomicSequencer = subjectAtomicSequencer;
    }

    public SubjectAtomicSequencer getSubjectAtomicSequencer() {
        return subjectAtomicSequencer;
    }

    public void setServiceAtomicSequencer(ServiceAtomicSequencer serviceAtomicSequencer) {
        this.serviceAtomicSequencer = serviceAtomicSequencer;
    }

    public ServiceAtomicSequencer getServiceAtomicSequencer() {
        return serviceAtomicSequencer;
    }

    public void setActivityAtomicSequencer(ActivityAtomicSequencer activityAtomicSequencer) {
        this.activityAtomicSequencer = activityAtomicSequencer;
    }

    public ActivityAtomicSequencer getActivityAtomicSequencer() {
        return activityAtomicSequencer;
    }
}
