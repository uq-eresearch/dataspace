package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.util.FeedHelper;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Service;
import net.metadata.dataspace.data.model.ServiceVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.activation.MimeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:22:53 PM
 */
public class ServiceAdapter extends AbstractEntityCollectionAdapter<Service> {

    private Logger logger = Logger.getLogger(getClass());
    private ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
//    private EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        MimeType mimeType = request.getContentType();
        String baseType = mimeType.getBaseType();
        if (baseType.equals(Constants.JSON_MIMETYPE)) {
            return postMedia(request);
        } else if (mimeType.getBaseType().equals(Constants.ATOM_MIMETYPE)) {
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                Service service = entityCreator.getNextService();
                ServiceVersion serviceVersion = entityCreator.getNextServiceVersion(service);
                boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(serviceVersion, entry);
                if (!isValidEntry) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    transaction.begin();
                    serviceVersion.setParent(service);
                    service.getVersions().add(serviceVersion);
                    Date now = new Date();
                    serviceVersion.setUpdated(now);
                    service.setUpdated(now);
                    entityManager.persist(serviceVersion);
                    entityManager.persist(service);
                    furtherUpdate(entry, serviceVersion);
                    transaction.commit();
                    Entry createdEntry = AdapterHelper.getEntryFromService(serviceVersion, true);
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

    @Override
    public ResponseContext postMedia(RequestContext request) {
        MimeType mimeType = request.getContentType();
        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            try {
                String jsonString = AdapterHelper.getJsonString(request.getInputStream());
                if (jsonString == null) {
                    return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                } else {
                    Service service = entityCreator.getNextService();
                    ServiceVersion serviceVersion = entityCreator.getNextServiceVersion(service);
                    if (!assembleServiceFromJson(service, serviceVersion, jsonString)) {
                        return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                    }
                    Entry createdEntry = AdapterHelper.getEntryFromService(serviceVersion, true);
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

    @Override
    public ResponseContext putEntry(RequestContext request) {
        logger.info("Updating Entry");
        String mimeBaseType = request.getContentType().getBaseType();
        if (mimeBaseType.equals(Constants.JSON_MIMETYPE)) {
            putMedia(request);
        } else if (mimeBaseType.equals(Constants.ATOM_MIMETYPE)) {
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                Entry entry = getEntryFromRequest(request);
                String uriKey = AdapterHelper.getEntryID(request);
                Service service = serviceDao.getByKey(uriKey);
                if (service == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (service.isActive()) {
                        ServiceVersion serviceVersion = entityCreator.getNextServiceVersion(service);
                        boolean isValidEntry = AdapterHelper.isValidVersionFromEntry(serviceVersion, entry);
                        if (!isValidEntry) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        } else {
                            transaction.begin();
                            service.getVersions().add(serviceVersion);
                            serviceVersion.setParent(service);
                            furtherUpdate(entry, serviceVersion);
                            entityManager.merge(service);
                            transaction.commit();
                            Entry createdEntry = AdapterHelper.getEntryFromService(serviceVersion, true);
                            return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                        }
                    } else {
                        return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
                    }
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
        return getEntry(request);
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        logger.info("Updating Media Entry");
        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot get inputstream from request.", e);
                return ProviderHelper.servererror(request, e);
            }
            String serviceAsJsonString = AdapterHelper.getJsonString(inputStream);
            if (serviceAsJsonString == null) {
                return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
            } else {
                String uriKey = AdapterHelper.getEntryID(request);
                Service service = serviceDao.getByKey(uriKey);
                if (service == null) {
                    return ProviderHelper.notfound(request);
                } else {
                    if (service.isActive()) {
                        ServiceVersion serviceVersion = entityCreator.getNextServiceVersion(service);
                        if (!assembleServiceFromJson(service, serviceVersion, serviceAsJsonString)) {
                            return ProviderHelper.badrequest(request, Constants.HTTP_STATUS_400);
                        }
                        Entry createdEntry = AdapterHelper.getEntryFromService(serviceVersion, false);
                        return AdapterHelper.getContextResponseForGetEntry(request, createdEntry);
                    } else {
                        return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
                    }
                }
            }
        } else {
            return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        Service service = serviceDao.getByKey(uriKey);
        if (service == null) {
            return ProviderHelper.notfound(request);
        } else {
            serviceDao.refresh(service);
            if (service.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete party entry");
                    return ProviderHelper.servererror(request, e);
                }
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
            }
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = AdapterHelper.getEntryID(request);
        Service service = serviceDao.getByKey(uriKey);
        if (service == null) {
            return ProviderHelper.notfound(request);
        } else {
            serviceDao.refresh(service);
            if (service.isActive()) {
                String versionKey = AdapterHelper.getEntryVersionID(request);
                ServiceVersion serviceVersion;
                if (versionKey != null) {
                    serviceVersion = serviceDao.getByVersion(uriKey, versionKey);
                } else {
                    serviceVersion = service.getVersions().first();
                }
                Entry entry = AdapterHelper.getEntryFromService(serviceVersion, versionKey == null);
                return AdapterHelper.getContextResponseForGetEntry(request, entry);
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, Constants.HTTP_STATUS_410);
            }
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType != null) {
            if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "service.jsp");
            } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return ProviderHelper.notsupported(request, Constants.HTTP_STATUS_415);
            }
        } else {
            String accept = request.getAccept();
            if (accept.equals(Constants.ATOM_FEED_MIMETYPE)) {
                return super.getFeed(request);
            } else {
                return FeedHelper.getHtmlRepresentationOfFeed(request, "service.jsp");
            }
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        Service latestService = serviceDao.getMostRecentUpdated();
        if (latestService != null) {
            serviceDao.refresh(latestService);
            feed.setUpdated(latestService.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        String representationMimeType = FeedHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.HTML_MIME_TYPE) || acceptHeader.equals(Constants.ATOM_FEED_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.HTML_MIME_TYPE;
            }
        }
        String atomFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "?repr=" + Constants.ATOM_FEED_MIMETYPE;
        String htmlFeedUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES;
        if (representationMimeType.equals(Constants.HTML_MIME_TYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
            FeedHelper.prepareFeedAlternateLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
        } else if (representationMimeType.equals(Constants.ATOM_FEED_MIMETYPE)) {
            FeedHelper.prepareFeedSelfLink(feed, atomFeedUrl, Constants.ATOM_FEED_MIMETYPE);
            FeedHelper.prepareFeedAlternateLink(feed, htmlFeedUrl, Constants.HTML_MIME_TYPE);
        }

        feed.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle() + ": " + Constants.TITLE_FOR_SERVICES);
        Iterable<Service> entries = getEntries(request);
        if (entries != null) {
            for (Service entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_ENTRY_MIMETYPE, Constants.JSON_MIMETYPE};
    }

    @Override
    public Service postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext request) throws ResponseContextException {
        serviceDao.softDelete(key);
    }

    @Override
    public Object getContent(Service entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    @Override
    public Iterable<Service> getEntries(RequestContext request) throws ResponseContextException {
        return serviceDao.getAllActive();
    }

    @Override
    public Service getEntry(String key, RequestContext request) throws ResponseContextException {
        Service service = serviceDao.getByKey(key);
        if (service != null) {
            serviceDao.refresh(service);
        }
        return service;

    }

    @Override
    public String getId(Service entry) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
    }

    @Override
    public String getName(Service entry) throws ResponseContextException {
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + entry.getUriKey();
    }

    @Override
    public String getTitle(Service entry) throws ResponseContextException {
        return entry.getTitle();
    }

    @Override
    public Date getUpdated(Service entry) throws ResponseContextException {
        return entry.getUpdated();
    }

    @Override
    public void putEntry(Service entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext request) {
        return Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES;
    }

    @Override
    public String getTitle(RequestContext request) {
        return Constants.TITLE_FOR_SERVICES;
    }

    private void furtherUpdate(Entry entry, ServiceVersion serviceVersion) {
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

    private boolean assembleServiceFromJson(Service service, ServiceVersion serviceVersion, String jsonString) {
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

}
