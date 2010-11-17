package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 17/11/2010
 * Time: 9:58:34 AM
 */
public class JsonHelper {

    private static Logger logger = Logger.getLogger(HttpMethodHelper.class.getName());
    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static PartyDao partyDao = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private static SubjectDao subjectDao = RegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private static ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private static ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private static EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();

    public static void createRecordFromJson(Record record, Version version, String json) throws ResponseContextException {
        if (record == null || version == null || json == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        } else {
            if (record instanceof Activity) {
                assembleActivityFromJson((Activity) record, (ActivityVersion) version, json);
            } else if (record instanceof Collection) {
                assembleCollectionFromJson((Collection) record, (CollectionVersion) version, json);
            } else if (record instanceof Party) {
                assembleValidPartyFromJson((Party) record, (PartyVersion) version, json);
            } else if (record instanceof Service) {
                assembleServiceFromJson((Service) record, (ServiceVersion) version, json);
            }
        }
    }

    private static void assembleActivityFromJson(Activity activity, ActivityVersion activityVersion, String activityAsJsonString) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(activityAsJsonString);
            activityVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            activityVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            activityVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            Date now = new Date();
            activityVersion.setUpdated(now);
            activity.setUpdated(now);
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            activityVersion.setAuthors(persons);

            if (activityVersion.getId() == null) {
                entityManager.persist(activity);
            }

            JSONArray collections = jsonObj.getJSONArray(Constants.ELEMENT_NAME_HAS_OUTPUT);
            for (int i = 0; i < collections.length(); i++) {
                Collection collection = collectionDao.getByKey(collections.getString(i));
                if (collection != null) {
                    collection.getOutputOf().add(activity);
                    activityVersion.getHasOutput().add(collection);
                    entityManager.merge(collection);
                }
            }

            JSONArray parties = jsonObj.getJSONArray(Constants.ELEMENT_NAME_HAS_PARTICIPANT);
            for (int i = 0; i < parties.length(); i++) {
                Party party = partyDao.getByKey(parties.getString(i));
                if (party != null) {
                    party.getParticipantIn().add(activity);
                    activityVersion.getHasParticipant().add(party);
                    entityManager.merge(party);
                }
            }
            activity.getVersions().add(activityVersion);
            activityVersion.setParent(activity);
            entityManager.merge(activity);
            transaction.commit();
        } catch (Throwable ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ResponseContextException(500, ex);
        }
    }

    private static void assembleCollectionFromJson(Collection collection, CollectionVersion collectionVersion, String jsonString) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(jsonString);
            collectionVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            collectionVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            collectionVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            Date now = new Date();
            collectionVersion.setUpdated(now);
            collection.setUpdated(now);
            collectionVersion.setLocation(jsonObj.getString(Constants.ELEMENT_NAME_LOCATION));
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            collectionVersion.setAuthors(persons);
            if (collection.getId() == null) {
                entityManager.persist(collection);
            }
            JSONArray collectors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_COLLECTOR);
            for (int i = 0; i < collectors.length(); i++) {
                Party party = partyDao.getByKey(collectors.getString(i));
                if (party != null) {
                    party.getCollectorOf().add(collection);
                    collectionVersion.getCollector().add(party);
                    entityManager.merge(party);
                }
            }
            //TODO link to Activity and Service here
            JSONArray subjectArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUBJECT);
            for (int i = 0; i < subjectArray.length(); i++) {
                String vocabulary = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VOCABULARY);
                String value = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VALUE);
                if (vocabulary == null || value == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    Subject subject = subjectDao.getSubject(vocabulary, value);
                    if (subject == null) {
                        subject = entityCreator.getNextSubject();
                        subject.setVocabulary(vocabulary);
                        subject.setValue(value);
                        entityManager.persist(subject);
                    }
                    collectionVersion.getSubjects().add(subject);
                    entityManager.merge(subject);
                }
            }
            collection.getVersions().add(collectionVersion);
            collectionVersion.setParent(collection);
            entityManager.merge(collection);
            transaction.commit();
        } catch (Throwable ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ResponseContextException(500, ex);
        }
    }

    private static void assembleValidPartyFromJson(Party party, PartyVersion partyVersion, String jsonString) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(jsonString);
            partyVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            partyVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            partyVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            Date now = new Date();
            partyVersion.setUpdated(now);
            party.setUpdated(now);
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            partyVersion.setAuthors(persons);

            if (party.getId() == null) {
                entityManager.persist(party);
            }
            if (partyVersion.getId() == null) {
                entityManager.persist(partyVersion);
            }

            JSONArray collectionArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_COLLECTOR_OF);
            for (int i = 0; i < collectionArray.length(); i++) {
                net.metadata.dataspace.data.model.base.Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getCollector().add(party);
                    partyVersion.getCollectorOf().add(collection);
                    entityManager.merge(collection);
                }
            }
            //TODO link to Activity here
            JSONArray subjectArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUBJECT);
            for (int i = 0; i < subjectArray.length(); i++) {
                String vocabulary = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VOCABULARY);
                String value = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VALUE);
                if (vocabulary == null || value == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    Subject subject = subjectDao.getSubject(vocabulary, value);
                    if (subject == null) {
                        subject = entityCreator.getNextSubject();
                        subject.setVocabulary(vocabulary);
                        subject.setValue(value);
                        entityManager.persist(subject);
                    }
                    partyVersion.getSubjects().add(subject);
                    entityManager.merge(subject);
                }
            }
            party.getVersions().add(partyVersion);
            partyVersion.setParent(party);
            entityManager.merge(party);
            transaction.commit();
        } catch (Throwable ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ResponseContextException(500, ex);
        }
    }

    private static void assembleServiceFromJson(Service service, ServiceVersion serviceVersion, String jsonString) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            JSONObject jsonObj = new JSONObject(jsonString);
            serviceVersion.setTitle(jsonObj.getString(Constants.ELEMENT_NAME_TITLE));
            serviceVersion.setSummary(jsonObj.getString(Constants.ELEMENT_NAME_SUMMARY));
            serviceVersion.setContent(jsonObj.getString(Constants.ELEMENT_NAME_CONTENT));
            serviceVersion.setLocation(jsonObj.getString(Constants.ELEMENT_NAME_LOCATION));
            Date now = new Date();
            serviceVersion.setUpdated(now);
            service.setUpdated(now);
            JSONArray authors = jsonObj.getJSONArray(Constants.ELEMENT_NAME_AUTHORS);
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            serviceVersion.setAuthors(persons);

            if (service.getId() == null) {
                entityManager.persist(service);
            }

            JSONArray collectionArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUPPORTED_BY);
            for (int i = 0; i < collectionArray.length(); i++) {
                Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getSupports().add(service);
                    serviceVersion.getSupportedBy().add(collection);
                    entityManager.merge(collection);
                }
            }

            service.getVersions().add(serviceVersion);
            serviceVersion.setParent(service);
            entityManager.merge(service);
            transaction.commit();
        } catch (Throwable ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ResponseContextException(500, ex);
        }
    }

}
