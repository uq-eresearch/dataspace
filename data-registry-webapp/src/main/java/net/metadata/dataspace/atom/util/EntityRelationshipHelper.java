package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.Control;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.context.ResponseContextException;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Set;

/**
 * Author: alabri
 * Date: 17/11/2010
 * Time: 9:54:40 AM
 */
public class EntityRelationshipHelper {

    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static PartyDao partyDao = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private static ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private static ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();

    public static void addRelations(Entry entry, Version version) throws ResponseContextException {
        if (version instanceof ActivityVersion) {
            addRelationsToActivity(entry, (ActivityVersion) version);
        } else if (version instanceof CollectionVersion) {
            addRelationsCollection(entry, (CollectionVersion) version);
        } else if (version instanceof PartyVersion) {
            addRelationsParty(entry, (PartyVersion) version);
        } else if (version instanceof ServiceVersion) {
            addRelationsService(entry, (ServiceVersion) version);
        }
    }

    private static void addRelationsToActivity(Entry entry, ActivityVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_HAS_OUTPUT);
        for (String key : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(key);
            if (collection != null) {
                collection.getOutputOf().add(version.getParent());
                version.getHasOutput().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> partyUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_HAS_PARTICIPANT);
        for (String partyKey : partyUriKeys) {
            Party party = partyDao.getByKey(partyKey);
            if (party != null) {
                party.getParticipantIn().add(version.getParent());
                version.getHasParticipant().add(party);
                entityManager.merge(party);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
        version.getParent().setUpdated(now);
    }

    private static void addRelationsCollection(Entry entry, CollectionVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectorUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_CREATOR);
        for (String uriKey : collectorUriKeys) {
            Party party = partyDao.getByKey(uriKey);
            if (party != null) {
                party.getCollectorOf().add((Collection) version.getParent());
                version.getCollector().add(party);
                entityManager.merge(party);
            }
        }
        Set<String> outputOfUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_IS_OUTPUT_OF);
        for (String uriKey : outputOfUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasOutput().add((Collection) version.getParent());
                version.getOutputOf().add(activity);
                entityManager.merge(activity);
            }
        }
        Set<String> supportUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_IS_ACCESSED_VIA);
        for (String uriKey : supportUriKeys) {
            Service service = serviceDao.getByKey(uriKey);
            if (service != null) {
                service.getSupportedBy().add((Collection) version.getParent());
                version.getSupports().add(service);
                entityManager.merge(service);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
        version.getParent().setUpdated(now);
    }

    private static void addRelationsParty(Entry entry, PartyVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_IS_COLLECTOR_OF);
        for (String uriKey : collectionUriKeys) {
            net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getCollector().add(version.getParent());
                version.getCollectorOf().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> isParticipantInUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_IS_PARTICIPANT_IN);
        for (String uriKey : isParticipantInUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasParticipant().add(version.getParent());
                version.getParticipantIn().add(activity);
                entityManager.merge(activity);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
        version.getParent().setUpdated(now);
    }

    private static void addRelationsService(Entry entry, ServiceVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromLink(entry, Constants.REL_IS_SUPPORTED_BY);
        for (String uriKey : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getSupports().add(version.getParent());
                version.getSupportedBy().add(collection);
                entityManager.merge(collection);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
        version.getParent().setUpdated(now);
    }


    private static void setPublished(Entry entry, Version version) {
        Control control = entry.getControl();
        if (control != null && !control.isDraft()) {
            version.getParent().setPublished(version);
        }
    }
}
