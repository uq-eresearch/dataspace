package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.ActivityType;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.types.CollectionType;
import net.metadata.dataspace.data.model.types.ServiceType;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.context.ResponseContextException;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: alabri
 * Date: 17/11/2010
 * Time: 9:54:40 AM
 */
public class AdapterInputHelper {

    private static CollectionDao collectionDao = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao();
    private static AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();
    private static ActivityDao activityDao = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao();
    private static ServiceDao serviceDao = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao();
    private static final EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
    private static DaoManager daoManager = RegistryApplication.getApplicationContext().getDaoManager();

    public static void addRelations(Entry entry, Version version) throws ResponseContextException {
        if (version instanceof ActivityVersion) {
            addRelationsToActivity(entry, (ActivityVersion) version);
        } else if (version instanceof CollectionVersion) {
            addRelationsCollection(entry, (CollectionVersion) version);
        } else if (version instanceof AgentVersion) {
            addRelationsAgent(entry, (AgentVersion) version);
        } else if (version instanceof ServiceVersion) {
            addRelationsService(entry, (ServiceVersion) version);
        }
    }

    private static void addRelationsToActivity(Entry entry, ActivityVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_HAS_OUTPUT);
        addPages(version, entry);
        for (String key : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(key);
            if (collection != null) {
                Activity parent = version.getParent();
                version.getHasOutput().add(collection);
                collection.getOutputOf().add(parent);

                entityManager.merge(collection);
            }
        }
        Set<String> agentUriKeys = getUriKeysFromLink(entry, Constants.REL_HAS_PARTICIPANT);
        for (String agentKey : agentUriKeys) {
            Agent agent = agentDao.getByKey(agentKey);
            if (agent != null) {
                Activity parent = version.getParent();
                agent.getParticipantIn().add(parent);
                version.getHasParticipants().add(agent);
                entityManager.merge(agent);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private static void addRelationsCollection(Entry entry, CollectionVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        addPages(version, entry);
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }
        Set<Publication> publications = getPublications(entry);
        for (Publication publication : publications) {
            version.getReferencedBy().add(publication);
        }
        Set<String> collectorUriKeys = getUriKeysFromLink(entry, Constants.REL_CREATOR);
        for (String uriKey : collectorUriKeys) {
            Agent agent = agentDao.getByKey(uriKey);
            if (agent != null) {
                version.getCreators().add(agent);
                Collection parent = version.getParent();
                agent.getMade().add(parent);
                entityManager.merge(agent);
            }
        }

        Set<String> publishersUriKeys = getUriKeysFromLink(entry, Constants.REL_PUBLISHER);
        for (String uriKey : publishersUriKeys) {
            Agent publisher = agentDao.getByKey(uriKey);
            if (publisher != null) {
                Collection parent = version.getParent();
                version.getPublishers().add(publisher);
                publisher.getIsManagerOf().add(parent);
                entityManager.merge(publisher);
            }
        }
        Set<String> outputOfUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_OUTPUT_OF);
        for (String uriKey : outputOfUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                Collection parent = version.getParent();
                version.getOutputOf().add(activity);
                activity.getHasOutput().add(parent);
                entityManager.merge(activity);
            }
        }
        Set<String> supportUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_ACCESSED_VIA);
        for (String uriKey : supportUriKeys) {
            Service service = serviceDao.getByKey(uriKey);
            if (service != null) {
                Collection parent = version.getParent();
                version.getAccessedVia().add(service);
                service.getSupportedBy().add(parent);
                entityManager.merge(service);
            }
        }

        List<Element> extensions = entry.getExtensions(Constants.QNAME_RDFA_META);
        for (Element extension : extensions) {
            String property = extension.getAttributeValue("property");
            if (property.equals(Constants.REL_TEMPORAL)) {
                String content = extension.getAttributeValue("content");
                version.getTemporals().add(content);
            } else if (property.equals(Constants.REL_ACCESS_RIGHTS)) {
                String content = extension.getAttributeValue("content");
                version.getAccessRights().add(content);
            }
        }

        List<Element> geoRssPointExtensions = entry.getExtensions(Constants.QNAME_GEO_RSS_POINT);
        for (Element extension : geoRssPointExtensions) {
            version.getGeoRssPoints().add(extension.getText());
        }

        List<Element> geoRssBoxExtensions = entry.getExtensions(Constants.QNAME_GEO_RSS_BOX);
        for (Element extension : geoRssBoxExtensions) {
            version.getGeoRssBoxes().add(extension.getText());
        }

        List<Element> geoRssFeatureNameExtensions = entry.getExtensions(Constants.QNAME_GEO_RSS_FEATURE_NAME);
        for (Element extension : geoRssFeatureNameExtensions) {
            version.getGeoRssFeatureNames().add(extension.getText());
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private static void addRelationsAgent(Entry entry, AgentVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        //TODO add the mbox from the creating agent
        String email = entry.getAuthors().get(0).getEmail();
        version.getMboxes().add(email);
        addPages(version, entry);
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }
        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_MADE);
        for (String uriKey : collectionUriKeys) {
            net.metadata.dataspace.data.model.record.Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                Agent parent = version.getParent();
                collection.getCreators().add(parent);
                version.getMade().add(collection);
                entityManager.merge(collection);
            }
        }

        Set<String> publishedCollectionsUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_MANAGER_OF);
        for (String uriKey : publishedCollectionsUriKeys) {
            net.metadata.dataspace.data.model.record.Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                Agent parent = version.getParent();
                collection.getPublishers().add(parent);
                version.getIsManagerOf().add(collection);
                entityManager.merge(collection);
            }
        }
        Set<String> isParticipantInUriKeys = getUriKeysFromLink(entry, Constants.REL_CURRENT_PROJECT);
        for (String uriKey : isParticipantInUriKeys) {
            Activity activity = activityDao.getByKey(uriKey);
            if (activity != null) {
                Agent parent = version.getParent();
                activity.getHasParticipant().add(parent);
                version.getCurrentProjects().add(activity);
                entityManager.merge(activity);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private static void addRelationsService(Entry entry, ServiceVersion version) throws ResponseContextException {
        EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_SUPPORTED_BY);
        addPages(version, entry);
        for (String uriKey : collectionUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                Service parent = version.getParent();
                version.getSupportedBy().add(collection);
                collection.getAccessedVia().add(parent);
                entityManager.merge(collection);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    public static Version assembleAndValidateVersionFromEntry(Record record, Entry entry) throws ResponseContextException {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return null;
        } else {
            String content = entry.getContent();
            if (content == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
            }
            Version version = entityCreator.getNextVersion(record);
            version.setTitle(entry.getTitle());
            version.setDescription(content);
            version.setUpdated(new Date());
            addType(version, entry);
            return version;
        }
    }

    public static Source assembleAndValidateSourceFromEntry(Entry entry) throws ResponseContextException {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        } else {
            org.apache.abdera.model.Source abderaSource = entry.getSource();
            if (abderaSource == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
            }
            try {
                String sourceUri = entry.getSource().getId().toString();
                Source existingSource = daoManager.getSourceDao().getBySourceURI(sourceUri);
                if (existingSource == null) {
                    Source source = entityCreator.getNextSource();
                    source.setTitle(entry.getSource().getTitle());
                    source.setSourceURI(sourceUri);
                    source.setUpdated(new Date());
                    return source;
                } else {
                    return existingSource;
                }
            } catch (Throwable th) {
                throw new ResponseContextException(500, th);
            }
        }
    }

    public static void addDescriptionAuthors(Record record, List<Person> persons) throws ResponseContextException {
        try {
            for (Person person : persons) {
                String name = person.getName();
                String email = person.getEmail();
                String uri = person.getUri().toString();
                if (name == null) {
                    throw new ResponseContextException("Author missing name", 400);
                } else if (email == null) {
                    throw new ResponseContextException("Author missing email address", 400);
                } else if (uri == null) {
                    throw new ResponseContextException("Author missing uri", 400);
                } else {
                    String uriKey = OperationHelper.getEntityID(uri);
                    Agent agent = daoManager.getAgentDao().getByKey(uriKey);
                    if (agent != null) {
                        record.getAuthors().add(agent);
                    } else {
                        //TODO how do we add the agent now?
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract authors", 500);
        }
    }

    public static Set<Subject> getSubjects(Entry entry) throws ResponseContextException {
        Set<Subject> subjects = new HashSet<Subject>();
        try {
            List<Category> categories = entry.getCategories();
            for (Category category : categories) {
                if (!category.getScheme().toString().equals(Constants.NS_DCMITYPE)) {
                    String scheme = category.getScheme().toString();
                    String term = category.getTerm();
                    if (scheme != null && term != null) {
                        Subject subject = daoManager.getSubjectDao().getSubject(scheme, term);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                        }
                        subject.setTerm(term);
                        subject.setDefinedBy(scheme);
                        subject.setLabel(category.getLabel());
                        subjects.add(subject);
                    } else {
                        String label = category.getLabel();
                        Subject subject = daoManager.getSubjectDao().getSubject(Constants.SCHEME_KEYWORD, Constants.TERM_KEYWORD, label);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                        }
                        subject.setTerm(Constants.TERM_KEYWORD);
                        subject.setDefinedBy(Constants.SCHEME_KEYWORD);
                        subject.setLabel(category.getLabel());
                        subjects.add(subject);
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract subjects from entry", 400);
        }
        return subjects;
    }

    public static Set<Publication> getPublications(Entry entry) throws ResponseContextException {
        Set<Publication> publications = new HashSet<Publication>();
        try {
            List<Link> links = entry.getLinks(Constants.REL_RELATED);
            for (Link link : links) {
                String publicationUri = link.getHref().toString();
                String publicationTitle = link.getTitle();
                if (publicationUri != null && publicationTitle != null) {
                    Publication publication = entityCreator.getNextPublication();
                    publication.setPublicationURI(publicationUri);
                    publication.setTitle(publicationTitle);
                    publications.add(publication);
                } else {
                    throw new ResponseContextException("Publication contains no href or title attributes", 400);
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract publications from entry", 400);
        }
        return publications;
    }

    private static void setPublished(Entry entry, Version version) {
        Control control = entry.getControl();
        if (control != null && !control.isDraft()) {
            version.getParent().setPublished(version);
            version.getParent().setPublishDate(new Date());
        }
    }

    private static void addPages(Version version, Entry entry) throws ResponseContextException {
        List<Link> links = entry.getLinks(Constants.REL_PAGE);
        if (version instanceof CollectionVersion && links.isEmpty()) {
            throw new ResponseContextException(Constants.REL_PAGE + " link element is missing", 400);
        }
        for (Link link : links) {
            String page = link.getHref().toString();
            version.getPages().add(page);
        }
    }


    private static void addType(Version version, Entry entry) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        } else {
            if (version instanceof ActivityVersion) {
                //TODO this need to be retrieved from the entry
                ((ActivityVersion) version).setType(ActivityType.PROJECT);
            } else if (version instanceof AgentVersion) {
                //TODO this need to be retrieved from the entry
                ((AgentVersion) version).setType(AgentType.PERSON);
            } else if (version instanceof CollectionVersion) {
                //TODO this need to be retrieved from the entry
                ((CollectionVersion) version).setType(CollectionType.COLLECTION);
                ((CollectionVersion) version).setRights(entry.getRights());
            } else if (version instanceof ServiceVersion) {
                //TODO this need to be retrieved from the entry
                ((ServiceVersion) version).setType(ServiceType.SYNDICATE);
            }
        }
    }

    private static Set<String> getUriKeysFromLink(Entry entry, String rel) throws ResponseContextException {
        Set<String> uriKeys = new HashSet<String>();
        try {
            List<Link> links = entry.getLinks(rel);
            for (Link link : links) {
                String id = OperationHelper.getEntityID(link.getHref().toString());
                if (id != null) {
                    uriKeys.add(id);
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract href from link", 400);
        }
        return uriKeys;
    }
}
