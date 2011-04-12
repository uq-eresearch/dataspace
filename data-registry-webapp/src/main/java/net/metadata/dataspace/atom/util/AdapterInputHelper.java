package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.util.LDAPUtil;
import net.metadata.dataspace.data.access.*;
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
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.context.ResponseContextException;

import javax.naming.NamingEnumeration;
import javax.persistence.EntityManager;
import java.util.*;

import net.metadata.dataspace.data.model.record.Collection;

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
        List<Person> authors = entry.getAuthors();
        addPages(version, entry);
        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_HAS_OUTPUT);
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

        //Add the original id
        if (entry.getId() != null) {
            version.setOriginalId(entry.getId().toString());
        }

        //Add web pages
        addPages(version, entry);

        //Add subjects
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }

        //Add creators of this collection
        addCollectionCreator(version, entry.getAuthors());

        //Add publishers
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

        //Add outputof
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

        Set<String> relatedCollectionsUriKeys = getUriKeysFromLink(entry, Constants.REL_RELATED);
        for (String uriKey : relatedCollectionsUriKeys) {
            Collection collection = collectionDao.getByKey(uriKey);
            if (collection != null) {
                version.getRelations().add(collection);
                entityManager.merge(collection);
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

        List<Element> geoRssPolygonExtensions = entry.getExtensions(Constants.QNAME_GEO_RSS_POLYGON);
        for (Element extension : geoRssPolygonExtensions) {
            version.getGeoRssPolygons().add(extension.getText());
        }

        List<Link> links = entry.getLinks(Constants.REL_SPATIAL);
        for (Link link : links) {
            version.getGeoRssFeatureNames().add(link.getTitle());
        }

        Set<Publication> publications = getPublications(entry);
        for (Publication publication : publications) {
            version.getReferencedBy().add(publication);
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
                IRI uri = person.getUri();
                if (name == null) {
                    throw new ResponseContextException("Author missing name", 400);
                }
                if (email == null) {
                    throw new ResponseContextException("Author missing email address", 400);
                }
                if (uri != null) {
                    String uriKey = OperationHelper.getEntityID(uri.toString());
                    Agent agent = daoManager.getAgentDao().getByKey(uriKey);
                    if (agent != null) {
                        record.getAuthors().add(agent);
                    } else {
                        Agent newAgent = findOrCreateAgent(name, email);
                        if (newAgent == null) {
                            throw new ResponseContextException("Description Author cannot be found", 400);
                        } else {
                            record.getAuthors().add(newAgent);
                        }
                    }
                } else {
                    Agent newAgent = findOrCreateAgent(name, email);
                    if (newAgent == null) {
                        throw new ResponseContextException("Description Author cannot be found", 400);
                    } else {
                        record.getAuthors().add(newAgent);
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract authors", 500);
        }
    }

    public static void addCollectionCreator(CollectionVersion version, List<Person> persons) throws ResponseContextException {
        try {
            for (Person person : persons) {
                String name = person.getName();
                String email = person.getEmail();
                IRI uri = person.getUri();
                if (name == null) {
                    throw new ResponseContextException("Author missing name", 400);
                }
                if (email == null) {
                    throw new ResponseContextException("Author missing email address", 400);
                }
                if (uri != null) {
                    String uriKey = OperationHelper.getEntityID(uri.toString());
                    Agent agent = daoManager.getAgentDao().getByKey(uriKey);
                    if (agent != null) {
                        version.getCreators().add(agent);
                    } else {
                        Agent newAgent = findOrCreateAgent(name, email);
                        if (newAgent == null) {
                            throw new ResponseContextException("Author cannot be found", 400);
                        } else {
                            version.getCreators().add(newAgent);
                        }
                    }
                } else {
                    Agent newAgent = findOrCreateAgent(name, email);
                    if (newAgent == null) {
                        throw new ResponseContextException("Author cannot be found", 400);
                    } else {
                        version.getCreators().add(newAgent);
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract authors", 500);
        }
    }

    private static Set<Subject> getSubjects(Entry entry) throws ResponseContextException {
        Set<Subject> subjects = new HashSet<Subject>();
        try {
            List<Category> categories = entry.getCategories();
            for (Category category : categories) {
                IRI scheme = category.getScheme();
                String term = category.getTerm();
                if (scheme != null) {
                    if (scheme.equals(Constants.SCHEME_ANZSRC_FOR) || scheme.equals(Constants.SCHEME_ANZSRC_SEO) || scheme.equals(Constants.SCHEME_ANZSRC_TOA)) {
                        Subject subject = daoManager.getSubjectDao().getSubject(scheme.toString(), term);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                            subject.setTerm(term);
                            subject.setDefinedBy(scheme.toString());
                            subject.setLabel(category.getLabel());
                        }
                        subjects.add(subject);
                    }
                } else {
                    //It is a keyword
                    String label = category.getLabel();
                    Subject subject = daoManager.getSubjectDao().getSubject(Constants.SCHEME_KEYWORD, term, Constants.LABEL_KEYWORD);
                    if (subject == null) {
                        subject = entityCreator.getNextSubject();
                        subject.setTerm(term);
                        subject.setDefinedBy(Constants.SCHEME_KEYWORD);
                        subject.setLabel(Constants.LABEL_KEYWORD);
                    }
                    subjects.add(subject);
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract subjects from entry", 400);
        }
        return subjects;
    }


    private static Set<Publication> getPublications(Entry entry) throws ResponseContextException {
        Set<Publication> publications = new HashSet<Publication>();
        try {
            List<Link> links = entry.getLinks(Constants.REL_IS_REFERENCED_BY);
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

    private static Agent findOrCreateAgent(String name, String email) throws ResponseContextException {
        try {
            NamingEnumeration namingEnumeration = LDAPUtil.searchLDAPByEmail(email);
            Map<String, String> attributesAsMap = LDAPUtil.getAttributesAsMap(namingEnumeration);
            Agent agent = LDAPUtil.createAgent(attributesAsMap);
            if (agent == null) {
                agent = createBasicAgent(name, email);
            }
            return agent;
        } catch (Throwable th) {
            throw new ResponseContextException("Could not find agent", 500);
        }
    }

    private static Agent createBasicAgent(String name, String email) {
        Agent agent = ((Agent) entityCreator.getNextRecord(Agent.class));
        AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
        SourceDao sourceDao = RegistryApplication.getApplicationContext().getDaoManager().getSourceDao();
        Source systemSource = sourceDao.getBySourceURI(Constants.UQ_REGISTRY_URI_PREFIX);
//        transaction.begin();
//        String name = attributesMap.get("cn");
        version.setTitle(name);
        version.setDescription(name);
        Date now = new Date();
        version.setUpdated(now);
        version.setType(AgentType.PERSON);
        version.getMboxes().add(email);

        version.setParent(agent);
        agent.getVersions().add(version);
        version.getParent().setPublished(version);
        agent.setUpdated(now);

        agent.setUpdated(now);
        agent.setSource(systemSource);
        agent.getAuthors().add(agent);
        return agent;
    }
}
