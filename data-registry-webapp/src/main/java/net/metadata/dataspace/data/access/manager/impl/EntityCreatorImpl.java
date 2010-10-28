package net.metadata.dataspace.data.access.manager.impl;

import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.*;
import net.metadata.dataspace.data.sequencer.*;

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

    public Party getNextParty() {
        Party party = new Party();
        party.setAtomicNumber(partyAtomicSequencer.next());
        return party;
    }

    public Collection getNextCollection() {
        Collection collection = new Collection();
        collection.setAtomicNumber(collectionAtomicSequencer.next());
        return collection;
    }

    public Subject getNextSubject() {
        Subject subject = new Subject();
        subject.setAtomicNumber(subjectAtomicSequencer.next());
        return subject;
    }

    public Service getNextService() {
        Service service = new Service();
        service.setAtomicNumber(serviceAtomicSequencer.next());
        return service;
    }

    public Activity getNextActivity() {
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
