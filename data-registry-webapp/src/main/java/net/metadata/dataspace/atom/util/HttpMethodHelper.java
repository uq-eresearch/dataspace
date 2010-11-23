package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;

import javax.activation.MimeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Author: alabri
 * Date: 15/11/2010
 * Time: 1:44:04 PM
 */
public class HttpMethodHelper {

    private static Logger logger = Logger.getLogger(HttpMethodHelper.class.getName());
    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static PartyDao partyDao = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao();
    private static ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private static ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private static EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
    private static AuthorizationManager authorizationManager = RegistryApplication.getApplicationContext().getAuthorizationManager();
    private static AuthenticationManager authenticationManager = RegistryApplication.getApplicationContext().getAuthenticationManager();

    public static ResponseContext postEntry(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            MimeType mimeType = request.getContentType();
            String baseType = mimeType.getBaseType();
            if (baseType.equals(Constants.JSON_MIMETYPE)) {
                return postMedia(request, clazz);
            } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
                EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                Entry entry = getEntryFromRequest(request);
                Record record = entityCreator.getNextRecord(clazz);
                Version version = entityCreator.getNextVersion(record);
                boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(version, entry);
                if (!isValidEntry) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    try {
                        transaction.begin();
                        version.setParent(record);
                        record.getVersions().add(version);
                        Date now = new Date();
                        version.setUpdated(now);
                        record.setUpdated(now);
                        entityManager.persist(version);
                        entityManager.persist(record);
                        EntityRelationshipHelper.addRelations(entry, version);
                        transaction.commit();
                        Entry createdEntry = AdapterHelper.getEntryFromEntity(version, true);
                        return AdapterHelper.getContextResponseForPost(createdEntry);
                    } catch (PersistenceException th) {
                        logger.warn("Invalid Entry, Rolling back database", th);
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }
                        throw new ResponseContextException("Invalid Entry, Rolling back database", 400);
                    }
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        }
    }

    public static ResponseContext putEntry(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            logger.info("Updating Entry");
            String mimeBaseType = request.getContentType().getBaseType();
            if (mimeBaseType.equals(Constants.JSON_MIMETYPE)) {
                return putMedia(request, clazz);
            } else if (mimeBaseType.equals(Constants.ATOM_MIMETYPE)) {
                EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                Entry entry = getEntryFromRequest(request);
                String uriKey = AdapterHelper.getEntryID(request);
                Record record = getExistingRecord(uriKey, clazz);
                if (record == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                } else {
                    refreshRecord(record, clazz);
                    if (record.isActive()) {
                        Version version = entityCreator.getNextVersion(record);
                        boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(version, entry);
                        if (!isValidEntry) {
                            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                        } else {
                            if (authorizationManager.getAccessLevelForInstance(user, record).canUpdate()) {
                                try {
                                    transaction.begin();
                                    record.getVersions().add(version);
                                    version.setParent(record);
                                    EntityRelationshipHelper.addRelations(entry, version);
                                    entityManager.persist(version);
                                    entityManager.merge(record);
                                    transaction.commit();
                                    Entry updatedEntry = AdapterHelper.getEntryFromEntity(version, false);
                                    return AdapterHelper.getContextResponseForGetEntry(request, updatedEntry);
                                } catch (PersistenceException th) {
                                    logger.fatal("Invalid Entry, Rolling back database", th);
                                    if (transaction.isActive()) {
                                        transaction.rollback();
                                    }
                                    throw new ResponseContextException("Invalid Entry, Rolling back database", 400);
                                }
                            } else {
                                throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                            }
                        }
                    } else {
                        throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
                    }
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        }
    }

    public static ResponseContext postMedia(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            MimeType mimeType = request.getContentType();
            if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
                String json = getJson(request);
                if (json == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    Record record = entityCreator.getNextRecord(clazz);
                    Version version = entityCreator.getNextVersion(record);
                    JsonHelper.createRecordFromJson(record, version, json);
                    Entry createdEntry = AdapterHelper.getEntryFromEntity(version, true);
                    return AdapterHelper.getContextResponseForPost(createdEntry);
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        }
    }

    public static ResponseContext putMedia(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            logger.info("Updating Party as Media Entry");
            if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
                String json = getJson(request);
                if (json == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    String uriKey = AdapterHelper.getEntryID(request);
                    Record record = getExistingRecord(uriKey, clazz);
                    if (record == null) {
                        throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                    } else {
                        refreshRecord(record, clazz);
                        if (record.isActive()) {
                            if (authorizationManager.getAccessLevelForInstance(user, record).canUpdate()) {
                                Version partyVersion = entityCreator.getNextVersion(record);
                                JsonHelper.createRecordFromJson(record, partyVersion, json);
                                Entry createdEntry = AdapterHelper.getEntryFromEntity(partyVersion, false);
                                return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                            } else {
                                throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                            }
                        } else {
                            throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
                        }
                    }
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        }
    }

    public static ResponseContext deleteEntry(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            String uriKey = AdapterHelper.getEntryID(request);
            Record record = getExistingRecord(uriKey, clazz);
            if (record == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            } else {
                refreshRecord(record, clazz);
                if (record.isActive()) {
                    if (authorizationManager.getAccessLevelForInstance(user, record).canDelete()) {
                        deleteRecord(uriKey, clazz);
                        return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                    } else {
                        throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                    }
                } else {
                    throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
                }
            }
        }
    }

    public static ResponseContext getEntry(RequestContext request, Class clazz) throws ResponseContextException {
        String uriKey = AdapterHelper.getEntryID(request);
        Record record = getExistingRecord(uriKey, clazz);
        if (record == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
        } else {
            refreshRecord(record, clazz);
            if (record.isActive()) {
                String versionKey = AdapterHelper.getEntryVersionID(request);
                User user = authenticationManager.getCurrentUser(request);
                Version version;
                if (versionKey != null) {
                    if (authorizationManager.getAccessLevelForInstance(user, record).canUpdate()) {
                        if (versionKey.equals(Constants.TARGET_TYPE_VERSION_HISTORY)) {
                            Feed versionHistoryFeed = FeedHelper.createVersionFeed(request);
                            return FeedHelper.getVersionHistoryFeed(versionHistoryFeed, record);
                        } else if (versionKey.equals(Constants.TARGET_TYPE_WORKING_COPY)) {
                            version = record.getWorkingCopy();
                        } else {
                            version = partyDao.getByVersion(uriKey, versionKey);
                        }
                    } else {
                        throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                    }
                } else {
                    if (authorizationManager.getAccessLevelForInstance(user, record).canUpdate() && record.getPublished() == null) {
                        Feed versionHistoryFeed = FeedHelper.createVersionFeed(request);
                        return FeedHelper.getVersionHistoryFeed(versionHistoryFeed, record);
                    } else {
                        version = record.getPublished();
                    }
                }
                if (version == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                } else {
                    Entry entry = AdapterHelper.getEntryFromEntity(version, versionKey == null);
                    return AdapterHelper.getContextResponseForGetEntry(request, entry);
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
            }
        }
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

    private static Record getExistingRecord(String uriKey, Class clazz) {
        if (clazz.equals(Activity.class)) {
            return activityDao.getByKey(uriKey);
        } else if (clazz.equals(Collection.class)) {
            return collectionDao.getByKey(uriKey);
        } else if (clazz.equals(Party.class)) {
            return partyDao.getByKey(uriKey);
        } else if (clazz.equals(Service.class)) {
            return serviceDao.getByKey(uriKey);
        }
        return null;
    }

    private static void refreshRecord(Record record, Class clazz) throws ResponseContextException {
        try {
            if (clazz.equals(Activity.class)) {
                activityDao.refresh((Activity) record);
            } else if (clazz.equals(Collection.class)) {
                collectionDao.refresh((Collection) record);
            } else if (clazz.equals(Party.class)) {
                partyDao.refresh((Party) record);
            } else if (clazz.equals(Service.class)) {
                serviceDao.refresh((Service) record);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static void deleteRecord(String uriKey, Class clazz) throws ResponseContextException {
        try {
            if (clazz.equals(Activity.class)) {
                activityDao.softDelete(uriKey);
            } else if (clazz.equals(Collection.class)) {
                collectionDao.softDelete(uriKey);
            } else if (clazz.equals(Party.class)) {
                partyDao.softDelete(uriKey);
            } else if (clazz.equals(Service.class)) {
                serviceDao.softDelete(uriKey);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }


    private static String getJson(RequestContext request) throws ResponseContextException {
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (Throwable th) {
            logger.fatal("Cannot create inputstream from request.", th);
            throw new ResponseContextException("Could not obtain data from request", 400);
        }
        return AdapterHelper.getJsonString(inputStream);
    }

}
