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
            furtherUpdateActivity(entry, (ActivityVersion) version);
        } else if (version instanceof CollectionVersion) {
            furtherUpdateCollection(entry, (CollectionVersion) version);
        } else if (version instanceof PartyVersion) {
            furtherUpdateParty(entry, (PartyVersion) version);
        } else if (version instanceof ServiceVersion) {
            furtherUpdateService(entry, (ServiceVersion) version);
        }
    }

    private static void furtherUpdateActivity(Entry entry, ActivityVersion activityVersion) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_HAS_OUTPUT);
        for (String key : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(key);
            if (collection != null) {
                collection.getOutputOf().add(activityVersion.getParent());
                activityVersion.getHasOutput().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> partyUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_HAS_PARTICIPANT);
        for (String partyKey : partyUriKeys) {
            Party party = partyDao.getByKey(partyKey);
            if (party != null) {
                party.getParticipantIn().add(activityVersion.getParent());
                activityVersion.getHasParticipant().add(party);
                entityManager.merge(party);
            }
        }
        Date now = new Date();
        activityVersion.setUpdated(now);
        activityVersion.getParent().setUpdated(now);
    }

    private static void furtherUpdateCollection(Entry entry, CollectionVersion collectionVersion) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            collectionVersion.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectorUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_COLLECTOR);
        for (String uriKey : collectorUriKeys) {
            Party party = partyDao.getByKey(uriKey);
            if (party != null) {
                party.getCollectorOf().add((Collection) collectionVersion.getParent());
                collectionVersion.getCollector().add(party);
                entityManager.merge(party);
            }
        }
        Set<String> outputOfUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_IS_OUTPUT_OF);
        for (String uriKey : outputOfUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasOutput().add((Collection) collectionVersion.getParent());
                collectionVersion.getOutputOf().add(activity);
                entityManager.merge(activity);
            }
        }
        Set<String> supportUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_SUPPORTS);
        for (String uriKey : supportUriKeys) {
            Service service = serviceDao.getByKey(uriKey);
            if (service != null) {
                service.getSupportedBy().add((Collection) collectionVersion.getParent());
                collectionVersion.getSupports().add(service);
                entityManager.merge(service);
            }
        }
        Date now = new Date();
        collectionVersion.setUpdated(now);
        collectionVersion.getParent().setUpdated(now);
    }

    private static void furtherUpdateParty(Entry entry, PartyVersion partyVersion) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<Subject> subjects = AdapterHelper.getSubjects(entry);
        for (Subject subject : subjects) {
            partyVersion.getSubjects().add(subject);
            if (subject.getId() == null) {
                entityManager.persist(subject);
            }
        }
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_COLLECTOR_OF);
        for (String uriKey : collectionUriKeys) {
            net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getCollector().add(partyVersion.getParent());
                partyVersion.getCollectorOf().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> isParticipantInUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_IS_PARTICIPANT_IN);
        for (String uriKey : isParticipantInUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                activity.getHasParticipant().add(partyVersion.getParent());
                partyVersion.getParticipantIn().add(activity);
                entityManager.merge(activity);
            }
        }
        Date now = new Date();
        partyVersion.setUpdated(now);
        partyVersion.getParent().setUpdated(now);
    }

    private static void furtherUpdateService(Entry entry, ServiceVersion serviceVersion) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = AdapterHelper.getUriKeysFromExtension(entry, Constants.QNAME_SUPPORTED_BY);
        for (String uriKey : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                collection.getSupports().add(serviceVersion.getParent());
                serviceVersion.getSupportedBy().add(collection);
                entityManager.merge(collection);
            }
        }
        Date now = new Date();
        serviceVersion.setUpdated(now);
        serviceVersion.getParent().setUpdated(now);
    }

}
