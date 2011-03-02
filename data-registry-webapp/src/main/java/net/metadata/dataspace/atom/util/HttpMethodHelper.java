package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.*;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Author: alabri
 * Date: 15/11/2010
 * Time: 1:44:04 PM
 */
public class HttpMethodHelper {

    private static Logger logger = Logger.getLogger(HttpMethodHelper.class.getName());
    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();
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
            if (baseType.equals(Constants.MIME_TYPE_ATOM)) {
                Entry entry = getEntryFromRequest(request);
                Record record = entityCreator.getNextRecord(clazz);
                Version version = AdapterHelper.assembleAndValidateVersionFromEntry(record, entry);
                if (version == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                } else {
                    EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
                    EntityTransaction transaction = entityManager.getTransaction();
                    try {
                        if (transaction.isActive()) {
                            transaction.commit();
                        }
                        Source source = AdapterHelper.assembleAndValidateSourceFromEntry(entry);
                        transaction.begin();
                        if (source.getId() == null) {
                            entityManager.persist(source);
                        }
                        version.setParent(record);
                        Date now = new Date();
                        version.setUpdated(now);
                        record.setSource(source);
                        record.setLocatedOn(source);
                        record.setLicense(Constants.UQ_REGISTRY_LICENSE);
                        record.setRights(Constants.UQ_REGISTRY_RIGHTS);
                        record.getVersions().add(version);
                        record.setUpdated(now);
                        entityManager.persist(version);
                        entityManager.persist(record);
                        EntityRelationshipHelper.addRelations(entry, version);
                        AdapterHelper.addDescriptionAuthors(record, entry.getAuthors());
                        entityManager.merge(version);
                        entityManager.merge(record);
                        transaction.commit();
                        Entry createdEntry = AdapterHelper.getEntryFromEntity(version, true);
                        return AdapterHelper.getContextResponseForPost(createdEntry);
                    } catch (Exception th) {
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
            if (mimeBaseType.equals(Constants.MIME_TYPE_ATOM)) {
                Entry entry = getEntryFromRequest(request);
                String uriKey = AdapterHelper.getEntryID(request);
                Record record = getExistingRecord(uriKey, clazz);
                if (record == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                } else {
                    refreshRecord(record, clazz);
                    if (record.isActive()) {
                        Version version = AdapterHelper.assembleAndValidateVersionFromEntry(record, entry);
                        if (version == null) {
                            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
                        } else {
                            if (authorizationManager.getAccessLevelForInstance(user, record).canUpdate()) {
                                EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
                                EntityTransaction transaction = entityManager.getTransaction();
                                try {
                                    if (transaction.isActive()) {
                                        transaction.commit();
                                    }
                                    transaction.begin();
                                    record.getVersions().add(version);
                                    version.setParent(record);
                                    EntityRelationshipHelper.addRelations(entry, version);
                                    record.setUpdated(new Date());
                                    entityManager.persist(version);
                                    entityManager.merge(record);
                                    transaction.commit();
                                    Entry updatedEntry = AdapterHelper.getEntryFromEntity(version, false);
                                    return AdapterHelper.getContextResponseForGetEntry(request, updatedEntry, clazz);
                                } catch (Exception th) {
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

    /**
     * We do not support media posting
     *
     * @param request
     * @param clazz
     * @return
     * @throws ResponseContextException
     */
    public static ResponseContext postMedia(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
        }
    }

    /**
     * We do not support media putting
     *
     * @param request
     * @param clazz
     * @return
     * @throws ResponseContextException
     */
    public static ResponseContext putMedia(RequestContext request, Class clazz) throws ResponseContextException {
        User user = authenticationManager.getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
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
                            version = getVersion(uriKey, versionKey, clazz);
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
                    return AdapterHelper.getContextResponseForGetEntry(request, entry, clazz);
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
            }
        }
    }

    public static ResponseContext getFeed(RequestContext request, ResponseContext responseContext) throws ResponseContextException {
        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.MIME_TYPE_HTML)) {
                return FeedHelper.getHtmlRepresentationOfFeed(request, responseContext);
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        } else {
            return FeedHelper.getHtmlRepresentationOfFeed(request, responseContext);
        }
    }

    public static void addFeedDetails(Feed feed, RequestContext request, Class clazz) throws ResponseContextException {
        Record latestService = getLatestRecord(clazz);
        if (latestService != null) {
            refreshRecord(latestService, clazz);
            feed.setUpdated(latestService.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.MIME_TYPE_HTML) || acceptHeader.equals(Constants.MIME_TYPE_ATOM_FEED)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.MIME_TYPE_HTML;
            }
        }
        String atomFeedUrl = Constants.UQ_REGISTRY_URI_PREFIX + getPath(clazz) + "?repr=" + Constants.MIME_TYPE_ATOM_FEED;
        String htmlFeedUrl = Constants.UQ_REGISTRY_URI_PREFIX + getPath(clazz);
        if (representationMimeType.equals(Constants.MIME_TYPE_HTML)) {
            FeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.MIME_TYPE_HTML);
            FeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.MIME_TYPE_ATOM_FEED);
        } else if (representationMimeType.equals(Constants.MIME_TYPE_ATOM_FEED) || representationMimeType.equals(Constants.MIME_TYPE_ATOM)) {
            FeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.MIME_TYPE_ATOM_FEED);
            FeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.MIME_TYPE_HTML);
        }
        feed.setTitle(getTitle(clazz));
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
            if (entry_doc == null) {
                return null;
            }
            return entry_doc.getRoot();
        } catch (ParseException e) {
            throw new ResponseContextException(400, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
    }

    public static Iterable getRecords(RequestContext request, Class clazz) {
        User user = authenticationManager.getCurrentUser(request);
        List list;
        if (authorizationManager.canAccessWorkingCopy(user, Collection.class)) {
            if (clazz.equals(Activity.class)) {
                list = activityDao.getAllPublished();
                list.addAll(activityDao.getAllUnpublished());
            } else if (clazz.equals(Collection.class)) {
                list = collectionDao.getAllPublished();
                list.addAll(collectionDao.getAllUnpublished());
            } else if (clazz.equals(Agent.class)) {
                list = agentDao.getAllPublished();
                list.addAll(agentDao.getAllUnpublished());
            } else if (clazz.equals(Service.class)) {
                list = serviceDao.getAllPublished();
                list.addAll(serviceDao.getAllUnpublished());
            } else {
                return null;
            }
        } else {
            if (clazz.equals(Activity.class)) {
                list = activityDao.getAllPublished();
            } else if (clazz.equals(Collection.class)) {
                list = collectionDao.getAllPublished();
            } else if (clazz.equals(Agent.class)) {
                list = agentDao.getAllPublished();
            } else if (clazz.equals(Service.class)) {
                list = serviceDao.getAllPublished();
            } else {
                return null;
            }
        }
        return list;
    }

    public static List<Person> getAuthors(Record record, RequestContext request) throws ResponseContextException {
        Set<Agent> authors = record.getAuthors();
        List<Person> personList = new ArrayList<Person>();
        for (Agent author : authors) {
            Person person = request.getAbdera().getFactory().newAuthor();
            person.setName(author.getTitle());
            person.setEmail(author.getMBoxes().iterator().next());
            person.setUri(Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + author.getUriKey());
            personList.add(person);
        }
        return personList;
    }

    private static Record getExistingRecord(String uriKey, Class clazz) {
        if (clazz.equals(Activity.class)) {
            return activityDao.getByKey(uriKey);
        } else if (clazz.equals(Collection.class)) {
            return collectionDao.getByKey(uriKey);
        } else if (clazz.equals(Agent.class)) {
            return agentDao.getByKey(uriKey);
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
            } else if (clazz.equals(Agent.class)) {
                agentDao.refresh((Agent) record);
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
            } else if (clazz.equals(Agent.class)) {
                agentDao.softDelete(uriKey);
            } else if (clazz.equals(Service.class)) {
                serviceDao.softDelete(uriKey);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static Version getVersion(String uriKey, String versionKey, Class clazz) throws ResponseContextException {
        try {
            if (clazz.equals(Activity.class)) {
                return activityDao.getByVersion(uriKey, versionKey);
            } else if (clazz.equals(Collection.class)) {
                return collectionDao.getByVersion(uriKey, versionKey);
            } else if (clazz.equals(Agent.class)) {
                return agentDao.getByVersion(uriKey, versionKey);
            } else if (clazz.equals(Service.class)) {
                return serviceDao.getByVersion(uriKey, versionKey);
            }
            return null;
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static String getHtmlPage(Class clazz) throws ResponseContextException {
        try {
            return clazz.getSimpleName().toLowerCase();
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static String getPath(Class clazz) throws ResponseContextException {
        try {
            if (clazz.equals(Activity.class)) {
                return Constants.PATH_FOR_ACTIVITIES;
            } else if (clazz.equals(Collection.class)) {
                return Constants.PATH_FOR_COLLECTIONS;
            } else if (clazz.equals(Agent.class)) {
                return Constants.PATH_FOR_AGENTS;
            } else if (clazz.equals(Service.class)) {
                return Constants.PATH_FOR_SERVICES;
            }
            return null;
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static String getTitle(Class clazz) throws ResponseContextException {
        try {
            if (clazz.equals(Activity.class)) {
                return Constants.TITLE_FOR_ACTIVITIES;
            } else if (clazz.equals(Collection.class)) {
                return Constants.TITLE_FOR_COLLECTIONS;
            } else if (clazz.equals(Agent.class)) {
                return Constants.TITLE_FOR_AGENTS;
            } else if (clazz.equals(Service.class)) {
                return Constants.TITLE_FOR_SERVICES;
            }
            return null;
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static Record getLatestRecord(Class clazz) {
        if (clazz.equals(Activity.class)) {
            return activityDao.getMostRecentUpdated();
        } else if (clazz.equals(Collection.class)) {
            return collectionDao.getMostRecentUpdated();
        } else if (clazz.equals(Agent.class)) {
            return agentDao.getMostRecentUpdated();
        } else if (clazz.equals(Service.class)) {
            return serviceDao.getMostRecentUpdated();
        }
        return null;
    }

}
