package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.util.LDAPUtil;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.SourceAuthor;
import net.metadata.dataspace.data.model.context.Spatial;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.*;
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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.persistence.EntityManager;
import javax.xml.namespace.QName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Author: alabri
 * Date: 17/11/2010
 * Time: 9:54:40 AM
 */
@Transactional
public class AdapterInputHelper {

    private EntityCreator entityCreator;
    private DaoManager daoManager;

    private final Logger logger = Logger.getLogger(getClass());

    public void addRelations(Entry entry, Version<?> version, User currentUser) throws ResponseContextException, URISyntaxException {
        if (version instanceof ActivityVersion) {
            addRelationsToActivity(entry, (ActivityVersion) version);
        } else if (version instanceof CollectionVersion) {
            addRelationsCollection(entry, (CollectionVersion) version, currentUser);
        } else if (version instanceof AgentVersion) {
            addRelationsAgent(entry, (AgentVersion) version);
        } else if (version instanceof ServiceVersion) {
            addRelationsService(entry, (ServiceVersion) version);
        }
    }

    private void addRelationsToActivity(Entry entry, ActivityVersion version) throws ResponseContextException {
        EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();

        //Add the original id
        IRI entryId = entry.getId();
                if (entryId == null) {
            throw new ResponseContextException("Activity has empty id", 400);
        }
        if (entryId.toString().trim().endsWith(Constants.PATH_FOR_ACTIVITIES + "/ignore")) {
            String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();
            version.getParent().setOriginalId(parentUrl + "#");

        } else if (!entryId.toString().startsWith(Constants.UQ_REGISTRY_URI_PREFIX)){
            version.getParent().setOriginalId(entryId.toString());
        }

        addAlternativeTitles(version, entry);

        //Add web pages
        addPages(version, entry);

        //Add subjects
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }

        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_HAS_OUTPUT);
        for (String key : collectionUriKeys) {
            Collection collection = daoManager.getCollectionDao().getByKey(key);
            if (collection != null) {
                Activity parent = version.getParent();
                version.getHasOutput().add(collection);
                collection.getOutputOf().add(parent);
                entityManager.merge(collection);
            }
        }
        Set<String> agentUriKeys = getUriKeysFromLink(entry, Constants.REL_HAS_PARTICIPANT);
        for (String agentKey : agentUriKeys) {
            Agent agent = daoManager.getAgentDao().getByKey(agentKey);
            if (agent != null) {
                Activity parent = version.getParent();
                agent.getParticipantIn().add(parent);
                version.getHasParticipants().add(agent);
                entityManager.merge(agent);
            }
        }

        List<Element> extensions = entry.getExtensions(Constants.QNAME_RDFA_META);
        for (Element extension : extensions) {
            String property = extension.getAttributeValue("property");
            if (property.equals(Constants.REL_TEMPORAL)) {
                String content = extension.getAttributeValue("content");
                version.getTemporals().add(content);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private void addRelationsCollection(Entry entry, CollectionVersion version, User currentUser) throws ResponseContextException, URISyntaxException {
        EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();

        //Add the original id
        IRI entryId = entry.getId();
        if (entryId == null) {
            throw new ResponseContextException("Collection has empty id", 400);
        }
        if (entryId.toString().trim().endsWith(Constants.PATH_FOR_COLLECTIONS + "/ignore")) {
            String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
            version.getParent().setOriginalId(parentUrl + "#");

        } else if (!entryId.toString().startsWith(Constants.UQ_REGISTRY_URI_PREFIX)){
            version.getParent().setOriginalId(entryId.toString());
        }

        List<Link> emails = entry.getLinks(Constants.REL_MBOX);
        for (Link emailLink : emails) {
            IRI href = emailLink.getHref();
            if (href != null) {
                String token = "mailto:";
                String mailTo = href.toString();
                if (mailTo.startsWith(token)) {
                    version.getMboxes().add(mailTo.substring(mailTo.indexOf(":") + 1));
                } else {
                    version.getMboxes().add(mailTo);
                }
            }
        }

        addAlternativeTitles(version, entry);

        //Add web pages
        addPages(version, entry);

        //Add subjects
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }

        //Add creators of this collection
        addCollectionCreator(version, entry.getAuthors(), currentUser);
        entityManager.merge(version);
        //Add publishers
        addCollectionPublishers(version, entry.getLinks(Constants.REL_PUBLISHER), currentUser);
        entityManager.merge(version);

        //Add outputof
        Set<String> outputOfUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_OUTPUT_OF);
        for (String uriKey : outputOfUriKeys) {
            Activity activity = daoManager.getActivityDao().getByKey(uriKey);
            if (activity != null) {
                Collection parent = version.getParent();
                version.getOutputOf().add(activity);
                activity.getHasOutput().add(parent);
                entityManager.merge(activity);
            }
        }

        Set<String> supportUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_ACCESSED_VIA);
        for (String uriKey : supportUriKeys) {
            Service service = daoManager.getServiceDao().getByKey(uriKey);
            if (service != null) {
                Collection parent = version.getParent();
                version.getAccessedVia().add(service);
                service.getSupportedBy().add(parent);
                entityManager.merge(service);
            }
        }

        //related collections
        Set<String> relatedCollectionsUriKeys = getUriKeysFromLink(entry, Constants.REL_RELATED);
        for (String uriKey : relatedCollectionsUriKeys) {
            Collection collection = daoManager.getCollectionDao().getByKey(uriKey);
            if (collection != null) {
                version.getRelations().add(collection);
                Collection parent = version.getParent();
                CollectionVersion published = collection.getPublished();
                if (published != null) {
                    published.getRelations().add(parent);
                    entityManager.merge(published);
                }
                entityManager.merge(parent);
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

        Link license = entry.getLink(Constants.REL_LICENSE);
        if (license != null) {
            version.setLicense(license.getHref().toString());
        }

        QName qnameGeoRssPoint = Constants.QNAME_GEO_RSS_POINT;
        List<Element> geoRssPointExtensions = entry.getExtensions(qnameGeoRssPoint);
        for (Element extension : geoRssPointExtensions) {
            version.getGeoRssPoints().add(extension.getText());
        }

        List<Element> geoRssPolygonExtensions = entry.getExtensions(Constants.QNAME_GEO_RSS_POLYGON);
        for (Element extension : geoRssPolygonExtensions) {
            version.getGeoRssPolygons().add(extension.getText());
        }

        List<Link> links = entry.getLinks(Constants.REL_SPATIAL);
        for (Link link : links) {
        	Spatial spatial = new Spatial();
        	spatial.setName(link.getTitle());
        	spatial.setLocation(link.getHref().toURI());
            version.getSpatialCoverage().add(spatial);
        }
        //add publications
        Set<Publication> publications = getPublications(entry);
        for (Publication publication : publications) {
            version.getReferencedBy().add(publication);
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private void addRelationsAgent(Entry entry, AgentVersion version) throws ResponseContextException {
        EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();

        //Add the original id
        IRI entryId = entry.getId();
        if (entryId == null) {
            throw new ResponseContextException("Agent has empty id", 400);
        }
        if (entryId.toString().trim().endsWith(Constants.PATH_FOR_AGENTS + "/ignore")) {
            String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey();
            version.getParent().setOriginalId(parentUrl + "#");

        } else if (!entryId.toString().startsWith(Constants.UQ_REGISTRY_URI_PREFIX)){
            version.getParent().setOriginalId(entryId.toString());
        }

        List<Link> emails = entry.getLinks(Constants.REL_MBOX);
        if (emails.isEmpty()) {
            throw new ResponseContextException("Email (mbox) element is missing", 400);
        } else {
            for (Link emailLink : emails) {
                IRI href = emailLink.getHref();
                if (href != null) {
                    String token = "mailto:";
                    String mailTo = href.toString();
                    if (mailTo.startsWith(token)) {
                        version.getMboxes().add(mailTo.substring(mailTo.indexOf(":") + 1));
                    } else {
                        version.getMboxes().add(mailTo);
                    }
                }
            }
        }

        if (version.getType().equals(AgentType.PERSON)) {
            List<Element> extensions = entry.getExtensions(Constants.QNAME_RDFA_META);
            String fullNameTitle = null;
            String givenName = null;
            String familyName = null;
            for (Element extension : extensions) {
                String property = extension.getAttributeValue("property");
                if (property.equals(Constants.PROPERTY_TITLE)) {
                    fullNameTitle = extension.getAttributeValue("content");
                }
                if (property.equals(Constants.PROPERTY_GIVEN_NAME)) {
                    givenName = extension.getAttributeValue("content");
                }
                if (property.equals(Constants.PROPERTY_FAMILY_NAME)) {
                    familyName = extension.getAttributeValue("content");
                }
            }
            if (givenName != null && familyName != null) {
                FullName fullName = version.getParent().getFullName();
                if (fullName == null) {
                    fullName = entityCreator.getFullName();
                    fullName.setTitle(fullNameTitle);
                    fullName.setGivenName(givenName);
                    fullName.setFamilyName(familyName);
                    version.getParent().setFullName(fullName);
                } else {
                    fullName.setTitle(fullNameTitle);
                    fullName.setGivenName(givenName);
                    fullName.setFamilyName(familyName);
                }
            } else {
                throw new ResponseContextException("Agent missing full name elements given name, family name", 400);
            }
        }

        addAlternativeTitles(version, entry);

        //Add web pages
        addPages(version, entry);

        //add publications
        Set<Publication> publications = getAgentPublications(entry);
        for (Publication publication : publications) {
            version.getPublications().add(publication);
        }

        //Add subjects
        Set<Subject> subjects = getSubjects(entry);
        for (Subject subject : subjects) {
            version.getSubjects().add(subject);
        }

        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_MADE);
        for (String uriKey : collectionUriKeys) {
            net.metadata.dataspace.data.model.record.Collection collection = daoManager.getCollectionDao().getByKey(uriKey);
            if (collection != null) {
                Agent parent = version.getParent();
                collection.getCreators().add(parent);
                version.getMade().add(collection);
                entityManager.merge(collection);
            }
        }

        Set<String> publishedCollectionsUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_MANAGER_OF);
        for (String uriKey : publishedCollectionsUriKeys) {
            net.metadata.dataspace.data.model.record.Collection collection = daoManager.getCollectionDao().getByKey(uriKey);
            if (collection != null) {
                Agent parent = version.getParent();
                collection.getPublishers().add(parent);
                version.getIsManagerOf().add(collection);
                entityManager.merge(collection);
            }
        }

        Set<String> isParticipantInUriKeys = getUriKeysFromLink(entry, Constants.REL_CURRENT_PROJECT);
        for (String uriKey : isParticipantInUriKeys) {
            Activity activity = daoManager.getActivityDao().getByKey(uriKey);
            if (activity != null) {
                Agent parent = version.getParent();
                activity.getHasParticipant().add(parent);
                version.getCurrentProjects().add(activity);
                entityManager.merge(activity);
            }
        }

        Set<String> managedServiceInUriKeys = getUriKeysFromLink(entry, Constants.REL_MANAGES_SERVICE);
        for (String uriKey : managedServiceInUriKeys) {
            Service service = daoManager.getServiceDao().getByKey(uriKey);
            if (service != null) {
                Agent parent = version.getParent();
                service.getManagedBy().add(parent);
                version.getManagedServices().add(service);
                entityManager.merge(service);
            }
        }
        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    private void addRelationsService(Entry entry, ServiceVersion version) throws ResponseContextException {
        EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();

        //Add the original id
        IRI entryId = entry.getId();
        if (entryId == null) {
            throw new ResponseContextException("Service has empty id", 400);
        }
        if (entryId.toString().trim().endsWith(Constants.PATH_FOR_SERVICES + "/ignore")) {
            String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
            version.getParent().setOriginalId(parentUrl + "#");

        } else if (!entryId.toString().startsWith(Constants.UQ_REGISTRY_URI_PREFIX)){
            version.getParent().setOriginalId(entryId.toString());
        }

        addPages(version, entry);
        Set<String> collectionUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_SUPPORTED_BY);
        for (String uriKey : collectionUriKeys) {
            Collection collection = daoManager.getCollectionDao().getByKey(uriKey);
            if (collection != null) {
                Service parent = version.getParent();
                version.getSupportedBy().add(collection);
                collection.getAccessedVia().add(parent);
                entityManager.merge(collection);
            }
        }

        Set<String> agentsUriKeys = getUriKeysFromLink(entry, Constants.REL_IS_MANAGED_BY);
        for (String agentUriKey : agentsUriKeys) {
            Agent agent = daoManager.getAgentDao().getByKey(agentUriKey);
            if (agent != null) {
                Service parent = version.getParent();
                version.getManagedBy().add(agent);
                agent.getManagedServices().add(parent);
                entityManager.merge(agent);
            }
        }

        setPublished(entry, version);
        Date now = new Date();
        version.setUpdated(now);
    }

    public Version<?> assembleAndValidateVersionFromEntry(Record<?> record, Entry entry) throws ResponseContextException {
        if (entry == null) {
            throw new ResponseContextException("Empty Atom entry", 400);
        } else if(!ProviderHelper.isValidEntry(entry)) {
            throw new ResponseContextException("Invalid Atom entry", 400);
        } else {
            String content = entry.getContent();
            if (content == null) {
                throw new ResponseContextException("Content is null", 400);
            }
            Version<?> version = entityCreator.getNextVersion(record);
            version.setTitle(entry.getTitle());
            version.setDescription(content);
            version.setUpdated(new Date());
            addType(version, entry);
            return version;
        }
    }

    public Source assembleAndValidateSourceFromEntry(Entry entry) throws ResponseContextException {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            throw new ResponseContextException("Invalid entry", 400);
        } else {
            org.apache.abdera.model.Source abderaSource = entry.getSource();
            if (abderaSource == null) {
                throw new ResponseContextException("Source is null", 400);
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
            	logger.warn(th.getMessage(), th);
                throw new ResponseContextException(500, th);
            }
        }
    }

    public void addDescriptionAuthors(
    		Version<?> version,
    		List<Person> authors,
    		RequestContext request) throws ResponseContextException
    {
        try {
        	version.getDescriptionAuthors().clear();
        	if (authors.size() == 0) {
	            AuthenticationManager authenticationManager =
	            		RegistryApplication.getApplicationContext().getAuthenticationManager();
	            User currentUser = authenticationManager.getCurrentUser(request);
	            currentUser = daoManager.getUserDao()
	            		.getByUsername(currentUser.getUsername());
	            version.getDescriptionAuthors().add(
	            		new SourceAuthor(
	            				currentUser.getDisplayName(),
	            				currentUser.getEmail(), null));
        	} else {
            	for (Person person : authors) {
        			URI uri = person.getUri() == null ?
        					null : person.getUri().toURI();
        			version.getDescriptionAuthors().add(
        					new SourceAuthor(person.getName(),
        							person.getEmail(),
        							uri));
        		}
            	logger.debug("Inserted "+version.getDescriptionAuthors().size()+" description authors from source");
        	}
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Could not add description author", 500);
        }
    }

    public void addCollectionCreator(CollectionVersion version, List<Person> persons, User currentUser) throws ResponseContextException {
        for (Person person : persons) {
            String name = person.getName();
            String email = person.getEmail();
            IRI uri = person.getUri();
            if (name == null) {
                throw new ResponseContextException("Author missing name", 400);
            }
            if (uri != null) {
                EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();
                String uriKey = OperationHelper.getEntityID(uri.toString());
                Agent agent = daoManager.getAgentDao().getByKey(uriKey);
                if (agent != null) {
                    if (!agent.isActive()) {
                        agent.setActive(true);
                        agent.setUpdated(new Date());
                    }
                    version.getCreators().add(agent);
                    agent.getMade().add(version.getParent());
                    entityManager.merge(agent);
                } else {
                	if (email == null) {
                		throw new ResponseContextException("Author URI doesn't exist and missing email address", 400);
                	}
                	Agent newAgent = findOrCreateAgent(name, email, currentUser);
                    if (newAgent == null) {
                        throw new ResponseContextException("Author cannot be found", 400);
                    } else {
                        version.getCreators().add(newAgent);
                        newAgent.getMade().add(version.getParent());
                    }
                }
            } else {
            	if (email == null) {
            		throw new ResponseContextException("Author missing both email and uri address", 400);
            	}
                Agent newAgent = findOrCreateAgent(name, email, currentUser);
                if (newAgent == null) {
                    throw new ResponseContextException("Author cannot be found", 400);
                } else {
                    version.getCreators().add(newAgent);
                    newAgent.getMade().add(version.getParent());
                }
            }
        }
    }

    public void addCollectionPublishers(CollectionVersion version, List<Link> publishers, User currentUser) throws ResponseContextException {
        for (Link publisherLink : publishers) {
            String title = publisherLink.getTitle();
            if (title == null) {
                throw new ResponseContextException("Publisher link missing title", 400);
            }
            IRI href = publisherLink.getHref();
            if (href == null || href.toString().isEmpty()) {
                throw new ResponseContextException("Publisher link missing href", 400);
            } else {
                String hrefValue = href.toString().trim();
                String token = "mailto:";
                if (hrefValue.startsWith(token)) {
                    String email = hrefValue.substring(hrefValue.indexOf(":") + 1);
                    Agent newAgent = findOrCreateAgent(title, email, currentUser);
                    if (newAgent != null) {
                        version.getPublishers().add(newAgent);
                        newAgent.getIsManagerOf().add(version.getParent());
                    } else {
                        //cannot do much here
                    }
                } else {
                    //Href is uri
                    String uriKey = OperationHelper.getEntityID(hrefValue);
                    Agent agent = daoManager.getAgentDao().getByKey(uriKey);
                    if (agent != null) {
                        version.getPublishers().add(agent);
                        agent.getIsManagerOf().add(version.getParent());
                        EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();
                        entityManager.merge(agent);
                    } else {
                        //Cannot do much here
                    }
                }
            }
        }
    }

    private Set<Subject> getSubjects(Entry entry) throws ResponseContextException {
        Set<Subject> subjects = new HashSet<Subject>();
        try {
            List<Category> categories = entry.getCategories();
            for (Category category : categories) {
                IRI scheme = category.getScheme();
                String term = category.getTerm();
                if (scheme != null) {
                    Subject subject = daoManager.getSubjectDao().getSubject(scheme.toString(), term);
                    if (subject == null) {
                        subject = entityCreator.getNextSubject();
                        subject.setTerm(term);
                        subject.setDefinedBy(scheme.toString());
                        subject.setLabel(category.getLabel());
                    }
                    subjects.add(subject);
                } else {
                    //It is a keyword
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
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Cannot extract subjects from entry", 400);
        }
        return subjects;
    }


    private Set<Publication> getPublications(Entry entry) throws ResponseContextException {
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
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Cannot extract publications from entry", 400);
        }
        return publications;
    }

    private void setPublished(Entry entry, Version version) {
        Control control = entry.getControl();
        if (control != null && !control.isDraft()) {
            version.getParent().setPublished(version);
            version.getParent().setPublishDate(new Date());
        }
    }

    private void addAlternativeTitles(Version<?> version, Entry entry) {
        List<Element> extensions = entry.getExtensions(Constants.QNAME_RDFA_META);
        for (Element extension : extensions) {
            String property = extension.getAttributeValue("property");
            if (property.equals(Constants.REL_ALTERNATIVE)) {
                String content = extension.getAttributeValue("content");
                version.getAlternatives().add(content);
            }
        }
    }

    private void addPages(Version<?> version, Entry entry) throws ResponseContextException {
        List<Link> links = entry.getLinks(Constants.REL_PAGE);
        for (Link link : links) {
            String page = link.getHref().toString();
            version.getPages().add(page);
        }
    }

    private Set<Publication> getAgentPublications(Entry entry) throws ResponseContextException {
        Set<Publication> publications = new HashSet<Publication>();
        try {
            List<Link> links = entry.getLinks(Constants.REL_PUBLICATIONS);
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

    private void addType(Version<?> version, Entry entry) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException("Version is null", 400);
        } else {
            List<Link> links = entry.getLinks(Constants.REL_TYPE);
            if (links.size() != 1) {
                throw new ResponseContextException("Entry missing Type or assigned to more than one type", 400);
            } else {
                for (Link typeLink : links) {
                    String entryType = typeLink.getTitle();
                    if (entryType == null) {
                        throw new ResponseContextException("Entry type is missing label", 400);
                    } else {
                        entryType = entryType.toUpperCase();
                        if (version instanceof ActivityVersion) {
                            ActivityType type = ActivityType.valueOf(entryType);
                            if (type == null) {
                                throw new ResponseContextException("Entry type is invalid", 400);
                            } else {
                                ((ActivityVersion) version).setType(type);
                            }
                        } else if (version instanceof AgentVersion) {
                            AgentType type = AgentType.valueOf(entryType);
                            if (type == null) {
                                throw new ResponseContextException("Entry type is invalid", 400);
                            } else {
                                ((AgentVersion) version).setType(type);
                            }
                        } else if (version instanceof CollectionVersion) {
                            CollectionType type = CollectionType.valueOf(entryType);
                            if (type == null) {
                                throw new ResponseContextException("Entry type is invalid", 400);
                            } else {
                                ((CollectionVersion) version).setType(type);
                            }
                            ((CollectionVersion) version).setRights(entry.getRights());
                        } else if (version instanceof ServiceVersion) {
                            ServiceType type = ServiceType.valueOf(entryType);
                            if (type == null) {
                                throw new ResponseContextException("Entry type is invalid", 400);
                            } else {
                                ((ServiceVersion) version).setType(type);
                            }
                        }
                    }
                }
            }
        }
    }

    private Set<String> getUriKeysFromLink(Entry entry, String rel) throws ResponseContextException {
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

    private Agent findOrCreateAgent(String name, String email, User currentUser) throws ResponseContextException {
    	EntityManager entityManager = daoManager.getEntityManagerSource().getEntityManager();

    	//Find the agent in our system first
        Agent agent = daoManager.getAgentDao().getByEmail(email);
        if (agent == null) {
            NamingEnumeration<SearchResult> namingEnumeration = LDAPUtil.searchLDAPByEmail(email, currentUser);
            if (namingEnumeration != null) {
                Map<String, String> attributesAsMap;
				try {
					attributesAsMap = LDAPUtil.getAttributesAsMap(namingEnumeration);
				} catch (NamingException e) {
					throw new ResponseContextException("Could not find agent: " + email, 500);
				}
                agent = LDAPUtil.createAgent(attributesAsMap);
                if (agent == null) {
                    //Else create it from email and name
                    agent = createBasicAgent(name, email);
                }
            } else {
                agent = createBasicAgent(name, email);
            }
            //agent.setDescriptionAuthor(user);
            entityManager.persist(agent);
        } else {
            if (!agent.isActive()) {
                agent.setActive(true);
                agent.setUpdated(new Date());
            }
        }
        return agent;
    }

    private Agent createBasicAgent(String name, String email) {
        Agent agent = ((Agent) entityCreator.getNextRecord(Agent.class));
        AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
        SourceDao sourceDao = daoManager.getSourceDao();
        Source systemSource = sourceDao.getBySourceURI(Constants.UQ_REGISTRY_URI_PREFIX);
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
        version.setSource(systemSource);
        return agent;
    }

	public EntityCreator getEntityCreator() {
		return entityCreator;
	}

	public void setEntityCreator(EntityCreator entityCreator) {
		this.entityCreator = entityCreator;
	}

	public DaoManager getDaoManager() {
		return daoManager;
	}

	public void setDaoManager(DaoManager daoManager) {
		this.daoManager = daoManager;
	}

}
