package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.manager.EntityCreator;
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
    public Party getNextParty() {
        Party party = new Party();
        party.setAtomicNumber(partyAtomicSequencer.next());
        return party;
    }

    @Override
    public PartyVersion getNextPartyVersion(Party party) {
        PartyVersion partyVersion = new PartyVersion();
        AtomicInteger atomicInteger = new AtomicInteger(party.getVersions().size());
        partyVersion.setAtomicNumber(atomicInteger.incrementAndGet());
        return partyVersion;
    }

    @Override
    public Collection getNextCollection() {
        Collection collection = new Collection();
        collection.setAtomicNumber(collectionAtomicSequencer.next());
        return collection;
    }

    @Override
    public CollectionVersion getNextCollectionVersion(Collection collection) {
        CollectionVersion collectionVersion = new CollectionVersion();
        AtomicInteger atomicInteger = new AtomicInteger(collection.getVersions().size());
        collectionVersion.setAtomicNumber(atomicInteger.incrementAndGet());
        return collectionVersion;
    }

    @Override
    public Subject getNextSubject() {
        Subject subject = new Subject();
        subject.setAtomicNumber(subjectAtomicSequencer.next());
        return subject;
    }

    @Override
    public Service getNextService() {
        Service service = new Service();
        service.setAtomicNumber(serviceAtomicSequencer.next());
        return service;
    }

    @Override
    public ServiceVersion getNextServiceVersion(Service service) {
        ServiceVersion serviceVersion = new ServiceVersion();
        AtomicInteger atomicInteger = new AtomicInteger(service.getVersions().size());
        serviceVersion.setAtomicNumber(atomicInteger.incrementAndGet());
        return serviceVersion;
    }

    @Override
    public Activity getNextActivity() {
        Activity activity = new Activity();
        activity.setAtomicNumber(activityAtomicSequencer.next());
        return activity;
    }

    @Override
    public ActivityVersion getNextActivityVersion(Activity activity) {
        ActivityVersion collectionVersion = new ActivityVersion();
        AtomicInteger atomicInteger = new AtomicInteger(activity.getVersions().size());
        collectionVersion.setAtomicNumber(atomicInteger.incrementAndGet());
        return collectionVersion;
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
