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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimeType;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/11/2010
 * Time: 1:44:04 PM
 */
@Transactional
public class HttpMethodHelper {

    private static Logger logger = Logger.getLogger(HttpMethodHelper.class.getName());
    private CollectionDao collectionDao;
    private AgentDao agentDao;
    private ActivityDao activityDao;
    private ServiceDao serviceDao;
    private EntityCreator entityCreator;
    private AuthorizationManager<User> authorizationManager;
    private AuthenticationManager authenticationManager;
    private FeedOutputHelper feedOutputHelper;

	@Transactional
    public ResponseContext postEntry(RequestContext request, Class<? extends Record<?>> clazz) throws ResponseContextException {
		EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getEntityManagerSource().getEntityManager();

		User user = getAuthenticationManager().getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            MimeType mimeType = request.getContentType();
            String baseType = mimeType.getBaseType();
            if (baseType.equals(Constants.MIME_TYPE_ATOM)) {
                Entry entry = getEntryFromRequest(request);
                Record record = getEntityCreator().getNextRecord(clazz);
                Version version = AdapterInputHelper.assembleAndValidateVersionFromEntry(record, entry);
                if (version == null) {
                    throw new ResponseContextException("Version is null", 400);
                } else {
                    try {
                    	Source source = AdapterInputHelper.assembleAndValidateSourceFromEntry(entry);
                        if (source.getId() == null) {
                            entityManager.persist(source);
                        }
                        version.setParent(record);
                        Date now = new Date();
                        version.setUpdated(now);
                        List<Person> authors = entry.getSource().getAuthors();
                        AdapterInputHelper.addDescriptionAuthors(version, authors, request);
                        version.setSource(source);
                        //TODO these values (i.e. rights, license) should come from the entry
                        record.setLicense(Constants.UQ_REGISTRY_LICENSE);
                        record.setRights(Constants.UQ_REGISTRY_RIGHTS);
                        record.getVersions().add(version);
                        record.setUpdated(now);
                        entityManager.persist(version);
                        entityManager.persist(record);
                        AdapterInputHelper.addRelations(entry, version, user);
                        Entry createdEntry = AdapterOutputHelper.getEntryFromEntity(version, true);
                        return AdapterOutputHelper.getContextResponseForPost(createdEntry);
                    } catch (Exception th) {
                        logger.warn("Invalid Entry, Rolling back database", th);
                        throw new ResponseContextException(th.getMessage(), 400);
                    }
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
            }
        }
    }

    @Transactional
    public ResponseContext putEntry(RequestContext request, Class<?> clazz) throws ResponseContextException {
        User user = getAuthenticationManager().getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            logger.info("Updating Entry");
            String mimeBaseType = request.getContentType().getBaseType();
            if (mimeBaseType.equals(Constants.MIME_TYPE_ATOM)) {
                Entry entry = getEntryFromRequest(request);
                String uriKey = OperationHelper.getEntryID(request);
                Record record = getExistingRecord(uriKey, clazz);
                if (record == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                } else {
                    refreshRecord(record, clazz);
                    if (record.isActive()) {
                        Version version = AdapterInputHelper.assembleAndValidateVersionFromEntry(record, entry);
                        if (version == null) {
                            throw new ResponseContextException("Version is null", 400);
                        } else {
                            if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
                                EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getEntityManagerSource().getEntityManager();
                                try {
                                	Source source = AdapterInputHelper.assembleAndValidateSourceFromEntry(entry);
                                    if (source.getId() == null) {
                                        entityManager.persist(source);
                                    }
                                    record.getVersions().add(version);
                                    version.setParent(record);
                                    AdapterInputHelper.addRelations(entry, version, user);
                                    record.setUpdated(new Date());
                                    List<Person> authors = entry.getSource().getAuthors();
                                    AdapterInputHelper.addDescriptionAuthors(version, authors, request);
                                    version.setSource(source);
                                    entityManager.persist(version);
                                    entityManager.merge(record);
                                    Entry updatedEntry = AdapterOutputHelper.getEntryFromEntity(version, false);
                                    return AdapterOutputHelper.getContextResponseForPut(updatedEntry);
                                } catch (Exception th) {
                                    logger.fatal("Invalid Entry, Rolling back database", th);

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
    public ResponseContext postMedia(RequestContext request, Class<?> clazz) throws ResponseContextException {
        User user = getAuthenticationManager().getCurrentUser(request);
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
    public ResponseContext putMedia(RequestContext request, Class<?> clazz) throws ResponseContextException {
        User user = getAuthenticationManager().getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
        }
    }

    public ResponseContext deleteEntry(RequestContext request, Class<?> clazz) throws ResponseContextException {
        User user = getAuthenticationManager().getCurrentUser(request);
        if (user == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        } else {
            String uriKey = OperationHelper.getEntryID(request);
            Record record = getExistingRecord(uriKey, clazz);
            if (record == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            } else {
                refreshRecord(record, clazz);
                if (record.isActive()) {
                    if (getAuthorizationManager().getAccessLevelForInstance(user, record).canDelete()) {
                        deleteRecord(uriKey, clazz);
                        return OperationHelper.createResponse(200, Constants.HTTP_STATUS_200);
                    } else {
                        throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                    }
                } else {
                    throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
                }
            }
        }
    }

    public ResponseContext getEntry(RequestContext request, Class<?> clazz) throws ResponseContextException {
        String uriKey = OperationHelper.getEntryID(request);
        Record record = getExistingRecord(uriKey, clazz);
        if (record == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
        } else {
            refreshRecord(record, clazz);
            if (record.isActive()) {
                String versionKey = OperationHelper.getEntryVersionID(request);
                User user = getAuthenticationManager().getCurrentUser(request);
                Version version;
                if (versionKey != null) {
                    if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
                        if (versionKey.equals(Constants.TARGET_TYPE_VERSION_HISTORY)) {
                            Feed versionHistoryFeed = getFeedOutputHelper().createVersionFeed(request);
                            ResponseContext versionHistoryFeed1 = getFeedOutputHelper().getVersionHistoryFeed(request, versionHistoryFeed, record, clazz);
                            return versionHistoryFeed1;
                        } else {
                            version = getVersion(uriKey, versionKey, clazz);
                        }
                    } else {
                        throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                    }
                } else {
                    if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate() && record.getPublished() == null) {
//                        Feed versionHistoryFeed = getFeedOutputHelper().createVersionFeed(request);
//                        return getFeedOutputHelper().getVersionHistoryFeed(versionHistoryFeed, record);
                        version = record.getWorkingCopy();
                    } else {
                        version = record.getPublished();
                    }
                }
                if (version == null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
                } else {
                    Entry entry = AdapterOutputHelper.getEntryFromEntity(version, versionKey == null);
                    return AdapterOutputHelper.getContextResponseForGetEntry(request, entry, clazz);
                }
            } else {
                throw new ResponseContextException(Constants.HTTP_STATUS_410, 410);
            }
        }
    }

    public void addFeedDetails(Feed feed, RequestContext request, Class<?> clazz) throws ResponseContextException {
        Record latestService = getLatestRecord(clazz);
        if (latestService != null) {
            refreshRecord(latestService, clazz);
            feed.setUpdated(latestService.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = getFeedOutputHelper().getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader != null && (acceptHeader.equals(Constants.MIME_TYPE_HTML) || acceptHeader.equals(Constants.MIME_TYPE_ATOM_FEED))) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.MIME_TYPE_HTML;
            }
        }
        String atomFeedUrl = Constants.UQ_REGISTRY_URI_PREFIX + getPath(clazz) + "?repr=" + Constants.MIME_TYPE_ATOM_FEED;
        String htmlFeedUrl = Constants.UQ_REGISTRY_URI_PREFIX + getPath(clazz);
        if (representationMimeType.equals(Constants.MIME_TYPE_HTML)) {
            getFeedOutputHelper().prepareFeedSelfLink(feed, htmlFeedUrl, Constants.MIME_TYPE_HTML);
            getFeedOutputHelper().prepareFeedAlternateLink(feed, atomFeedUrl, Constants.MIME_TYPE_ATOM_FEED);
        } else if (representationMimeType.equals(Constants.MIME_TYPE_ATOM_FEED) || representationMimeType.equals(Constants.MIME_TYPE_ATOM)) {
            getFeedOutputHelper().prepareFeedSelfLink(feed, atomFeedUrl, Constants.MIME_TYPE_ATOM_FEED);
            getFeedOutputHelper().prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.MIME_TYPE_HTML);
        }
        feed.setTitle(getTitle(clazz));
    }

    /**
     * Retrieves the FOM Entry object from the request payload.
     */
    @SuppressWarnings("unchecked")
    private Entry getEntryFromRequest(RequestContext request) throws ResponseContextException {
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

    private Record getExistingRecord(String uriKey, Class<?> clazz) {
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

    private void refreshRecord(Record record, Class<?> clazz) throws ResponseContextException {
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

    private void deleteRecord(String uriKey, Class<?> clazz) throws ResponseContextException {
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

    private Version getVersion(String uriKey, String versionKey, Class<?> clazz) throws ResponseContextException {
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

    private String getPath(Class<?> clazz) throws ResponseContextException {
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

    private static String getTitle(Class<?> clazz) throws ResponseContextException {
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

    private Record getLatestRecord(Class<?> clazz) {
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

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	@Required
	public void setCollectionDao(CollectionDao collectionDao) {
		this.collectionDao = collectionDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	@Required
	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public ActivityDao getActivityDao() {
		return activityDao;
	}

	@Required
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	public ServiceDao getServiceDao() {
		return serviceDao;
	}

	@Required
	public void setServiceDao(ServiceDao serviceDao) {
		this.serviceDao = serviceDao;
	}

	public EntityCreator getEntityCreator() {
		return entityCreator;
	}

	public AuthorizationManager<User> getAuthorizationManager() {
		return authorizationManager;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	@Required
	public void setEntityCreator(EntityCreator entityCreator) {
		this.entityCreator = entityCreator;
	}

	@Required
	public void setAuthorizationManager(
			AuthorizationManager<User> authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	@Required
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public FeedOutputHelper getFeedOutputHelper() {
		return feedOutputHelper;
	}

	public void setFeedOutputHelper(FeedOutputHelper feedOutputHelper) {
		this.feedOutputHelper = feedOutputHelper;
	}

}
