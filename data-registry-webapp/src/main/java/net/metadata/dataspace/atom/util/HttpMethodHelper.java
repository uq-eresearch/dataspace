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
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.MimeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 15/11/2010
 * Time: 1:44:04 PM
 */
public class HttpMethodHelper {

    private static Logger logger = Logger.getLogger(HttpMethodHelper.class.getName());
    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static PartyDao partyDao = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private static SubjectDao subjectDao = RegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
    private static ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private static ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private static EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();

    public static ResponseContext postEntry(RequestContext request, Class clazz) {
        MimeType mimeType = request.getContentType();
        String baseType = mimeType.getBaseType();
        if (baseType.equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request, clazz);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                Record record = entityCreator.getNextRecord(clazz);
                Version version = entityCreator.getNextVersion(record);
                boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(version, entry);
                if (!isValidEntry) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    transaction.begin();
                    version.setParent(record);
                    record.getVersions().add(version);
                    Date now = new Date();
                    version.setUpdated(now);
                    record.setUpdated(now);
                    entityManager.persist(version);
                    entityManager.persist(record);
                    furtherUpdate(entry, version);
                    transaction.commit();
                    Entry createdEntry = AdapterHelper.getEntryFromEntity(version, true);
                    return AdapterHelper.getContextResponseForPost(createdEntry);
                }
            } catch (Exception e) {
                logger.fatal("Invalid Entry", e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
        }
    }

    public static ResponseContext postMedia(RequestContext request, Class clazz) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            try {
                String jsonString = AdapterHelper.getJsonString(request.getInputStream());
                if (jsonString == null) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    Record record = entityCreator.getNextRecord(clazz);
                    Version version = entityCreator.getNextVersion(record);
                    if (!createRecordFromJson(record, version, jsonString)) {
                        return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                    }
                    Entry createdEntry = AdapterHelper.getEntryFromEntity(version, true);
                    return AdapterHelper.getContextResponseForPost(createdEntry);
                }
            } catch (IOException e) {
                logger.fatal("Cannot get inputstream from request.");
                return ProviderHelper.servererror(request, e);
            }
        } else {
            return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
        }
    }

    private static boolean createRecordFromJson(Record record, Version version, String json) {
        if (record instanceof Activity) {
            return assembleActivityFromJson((Activity) record, (ActivityVersion) version, json);
        } else if (record instanceof Collection) {
            return assembleCollectionFromJson((Collection) record, (CollectionVersion) version, json);
        } else if (record instanceof Party) {
            return assembleValidPartyFromJson((Party) record, (PartyVersion) version, json);
        } else if (record instanceof Service) {
            return assembleServiceFromJson((Service) record, (ServiceVersion) version, json);
        }
        return false;
    }

    private static boolean assembleActivityFromJson(Activity activity, ActivityVersion activityVersion, String activityAsJsonString) {
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
        } catch (JSONException ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }

    private static boolean assembleCollectionFromJson(Collection collection, CollectionVersion collectionVersion, String jsonString) {
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
            JSONArray subjectArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUBJECT);
            for (int i = 0; i < subjectArray.length(); i++) {
                String vocabulary = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VOCABULARY);
                String value = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VALUE);
                if (vocabulary == null || value == null) {
                    return false;
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
        } catch (JSONException ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }

    private static boolean assembleValidPartyFromJson(Party party, PartyVersion partyVersion, String jsonString) {
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
            JSONArray subjectArray = jsonObj.getJSONArray(Constants.ELEMENT_NAME_SUBJECT);
            for (int i = 0; i < subjectArray.length(); i++) {
                String vocabulary = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VOCABULARY);
                String value = subjectArray.getJSONObject(i).getString(Constants.ATTRIBUTE_NAME_VALUE);
                if (vocabulary == null || value == null) {
                    return false;
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
        } catch (Exception ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }

    private static boolean assembleServiceFromJson(Service service, ServiceVersion serviceVersion, String jsonString) {
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
        } catch (Exception ex) {
            logger.warn("Could not assemble entry from JSON object");
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }

    private static void furtherUpdate(Entry entry, Version version) {
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

    private static void furtherUpdateActivity(Entry entry, ActivityVersion activityVersion) {
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

    private static void furtherUpdateCollection(Entry entry, CollectionVersion collectionVersion) {
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

    private static void furtherUpdateParty(Entry entry, PartyVersion partyVersion) {
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

    private static void furtherUpdateService(Entry entry, ServiceVersion serviceVersion) {
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

    /**
     * Retrieves the FOM Entry object from the request payload.
     */
    @SuppressWarnings("unchecked")
    private static Entry getEntryFromRequest(RequestContext request) throws ResponseContextException {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();

        Document<Entry> entry_doc;
        try {
            entry_doc = (Document<Entry>) request.getDocument(parser).clone();
        } catch (ParseException e) {
            throw new ResponseContextException(400, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
        if (entry_doc == null) {
            return null;
        }
        return entry_doc.getRoot();
    }
}
