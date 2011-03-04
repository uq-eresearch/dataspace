package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
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
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;

import java.util.*;

import net.metadata.dataspace.data.model.record.Collection;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
public class AdapterHelper {

    private static Logger logger = Logger.getLogger(AdapterHelper.class);
    private static final EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
    private static DaoManager daoManager = RegistryApplication.getApplicationContext().getDaoManager();

    public static String getEntityID(String fullUrl) {
        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }
        String[] segments = fullUrl.split("/");
        return UrlEncoding.decode(segments[segments.length - 1]);
    }

    public static String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY && request.getTarget().getType() != TargetType.get(Constants.TARGET_TYPE_VERSION)) {
            return null;
        }
        String fullUrl = request.getUri().toString();
        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }
        String[] segments = fullUrl.split("/");
        int segmentPos = segments.length - 1;
        if (request.getTarget().getType() == TargetType.get(Constants.TARGET_TYPE_VERSION)) {
            return UrlEncoding.decode(segments[segmentPos - 1]);
        }
        return UrlEncoding.decode(segments[segmentPos]);
    }

    public static String getEntryVersionID(RequestContext request) throws ResponseContextException {
        try {
            if (request.getTarget().getType() != TargetType.get(Constants.TARGET_TYPE_VERSION)) {
                return null;
            }
            String fullUrl = request.getUri().toString();
            if (fullUrl.contains("?")) {
                fullUrl = fullUrl.split("\\?")[0];
            }
            String[] segments = fullUrl.split("/");
            int segmentPos = segments.length - 1;
            return UrlEncoding.decode(segments[segmentPos]);
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    public static String getRepresentationMimeType(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY) {
            return null;
        }
        String fullUrl = request.getUri().toString();
        String representation = null;
        if (fullUrl.contains("?repr")) {
            representation = fullUrl.split("repr=")[1];
        }
        return representation;
    }

    public static Entry getEntryFromEntity(Version version, boolean isParentLevel) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        } else {
            if (version instanceof ActivityVersion) {
                return getEntryFromActivity((ActivityVersion) version, isParentLevel);
            } else if (version instanceof AgentVersion) {
                return getEntryFromAgent((AgentVersion) version, isParentLevel);
            } else if (version instanceof CollectionVersion) {
                return getEntryFromCollection((CollectionVersion) version, isParentLevel);
            } else if (version instanceof ServiceVersion) {
                return getEntryFromService((ServiceVersion) version, isParentLevel);
            }
        }
        return null;
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

    private static Entry getEntryFromActivity(ActivityVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_FOAF, Constants.TERM_ACTIVITY, version.getType().toString());
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            Set<Agent> agentSet = version.getHasParticipants();
            for (Agent agent : agentSet) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_HAS_PARTICIPANT);
                link.setTitle(agent.getTitle());
            }
            Set<Collection> collectionSet = version.getHasOutput();
            for (Collection collection : collectionSet) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_HAS_OUTPUT);
                link.setTitle(collection.getTitle());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addContributor(entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromAgent(AgentVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_FOAF, Constants.TERM_AGENT_AS_AGENT, version.getType().toString());
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getTerm(), sub.getDefinedBy(), sub.getLabel());
            }

            Set<Collection> collectionSet = version.getMade();
            for (Collection collection : collectionSet) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_MADE);
                link.setTitle(collection.getTitle());
            }

            Set<Collection> collections = version.getIsManagerOf();
            for (Collection collection : collections) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_MANAGER_OF);
                link.setTitle(collection.getTitle());
            }

            Set<Activity> activities = version.getCurrentProjects();
            for (Activity activity : activities) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_CURRENT_PROJECT);
                link.setTitle(activity.getTitle());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addContributor(entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromCollection(CollectionVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_DCMITYPE, Constants.TERM_COLLECTION, version.getType().toString());
            //Pages
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            //Subjects
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getTerm(), sub.getDefinedBy(), sub.getLabel());
            }
            //Creators
            Set<Agent> agents = version.getCreators();
            for (Agent agent : agents) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_CREATOR);
                link.setTitle(agent.getTitle());
            }
            //Publishers
            Set<Agent> publishers = version.getPublishers();
            for (Agent publisher : publishers) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + publisher.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_PUBLISHER);
                link.setTitle(publisher.getTitle());
            }
            //Is Output of
            Set<Activity> activities = version.getOutputOf();
            for (Activity activity : activities) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_OUTPUT_OF);
                link.setTitle(activity.getTitle());
            }
            //Accessed via
            Set<Service> services = version.getAccessedVia();
            for (Service service : services) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_ACCESSED_VIA);
                link.setTitle(service.getTitle());
            }
            //Publications
            Set<Publication> publications = version.getReferencedBy();
            for (Publication publication : publications) {
                String href = publication.getPublicationURI();
                Link link = entry.addLink(href, Constants.REL_RELATED);
                link.setTitle(publication.getTitle());
            }
            //Rights
            entry.setRights(version.getRights());
            //Access Rights
            Set<String> accessRights = version.getAccessRights();
            for (String accessRight : accessRights) {
                Element accessRightElement = entry.addExtension(Constants.QNAME_RDFA_META);
                accessRightElement.setAttributeValue("property", Constants.REL_ACCESS_RIGHTS);
                accessRightElement.setAttributeValue("content", accessRight);
            }
            //Temporal
            Set<String> temporals = version.getTemporals();
            for (String temporal : temporals) {
                Element temporalElement = entry.addExtension(Constants.QNAME_RDFA_META);
                temporalElement.setAttributeValue("property", Constants.REL_TEMPORAL);
                temporalElement.setAttributeValue("content", temporal);
            }
            //GeoRss Points
            Set<String> geoRssPoints = version.getGeoRssPoints();
            for (String geoRssPoint : geoRssPoints) {
                entry.addSimpleExtension(Constants.QNAME_GEO_RSS_POINT, geoRssPoint);
            }
            //GeoRss Boxes
            Set<String> geoRssBoxes = version.getGeoRssBoxes();
            for (String geoRssBox : geoRssBoxes) {
                entry.addSimpleExtension(Constants.QNAME_GEO_RSS_BOX, geoRssBox);
            }
            //GeoRss Feature Names
            Set<String> geoRssFeatureNames = version.getGeoRssFeatureNames();
            for (String geoRssFeatureName : geoRssFeatureNames) {
                entry.addSimpleExtension(Constants.QNAME_GEO_RSS_FEATURE_NAME, geoRssFeatureName);
            }


        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addContributor(entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromService(ServiceVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_VIVO, Constants.TERM_SERVICE, version.getType().toString());
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            Set<Collection> collectionSet = version.getSupportedBy();
            for (Collection collection : collectionSet) {
                String href = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_SUPPORTED_BY);
                link.setTitle(collection.getTitle());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addContributor(entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public static ResponseContext getContextResponseForGetEntry(RequestContext request, Entry entry, Class clazz) throws ResponseContextException {

        String accept = getAcceptHeader(request);

        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        responseContext.setLocation(entry.getId().toString());
        responseContext.setHeader("Vary", "Accept");
        if (accept.equals(Constants.MIME_TYPE_ATOM_ENTRY) || accept.equals(Constants.MIME_TYPE_ATOM)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            responseContext.setContentType(Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            PrettyWriter writer = new PrettyWriter();
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RDF)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RDF);
            String xslFilePath = "/files/xslt/rdf/atom2rdf-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_XHTML)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            if (request.getHeader("user-agent").toString().indexOf("MSIE ") > -1) {
                responseContext.setContentType(Constants.MIME_TYPE_XHTML);
            } else {
                responseContext.setContentType(Constants.MIME_TYPE_HTML);
            }
            String xslFilePath = "/files/xslt/xhtml/atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath, request);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RIFCS)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RIFCS);
            String xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else {
            return ProviderHelper.createErrorResponse(new Abdera(), 415, Constants.HTTP_STATUS_415);
        }

        return responseContext;
    }

    private static String getAcceptHeader(RequestContext request) {
        String representationMimeType = AdapterHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader != null) {
                if (acceptHeader.contains(Constants.MIME_TYPE_ATOM_ENTRY) || acceptHeader.contains(Constants.MIME_TYPE_ATOM)) {
                    representationMimeType = Constants.MIME_TYPE_ATOM;
                } else if (acceptHeader.contains(Constants.MIME_TYPE_RDF)) {
                    representationMimeType = Constants.MIME_TYPE_RDF;
                } else if (acceptHeader.contains(Constants.MIME_TYPE_RIFCS)) {
                    representationMimeType = Constants.MIME_TYPE_RIFCS;
                } else {
                    representationMimeType = Constants.MIME_TYPE_XHTML;
                }
            } else {
                representationMimeType = Constants.MIME_TYPE_XHTML;
            }
        }
        return representationMimeType;
    }

    public static ResponseContext getContextResponseForPost(Entry entry) throws ResponseContextException {
        try {
            ResponseContext responseContext = ProviderHelper.returnBase(entry, 201, entry.getUpdated());
            responseContext.setEntityTag(ProviderHelper.calculateEntityTag(entry));
            responseContext.setLocation(entry.getId().toString());
            return responseContext;
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
    }

    private static void prepareSelfLink(Entry entry, String href) throws ResponseContextException {
        try {
            Link selfLink = entry.getSelfLink();
            if (selfLink == null) {
                selfLink = entry.addLink(entry.getId().toString());
            }
            selfLink.setHref(href);
            selfLink.setRel(Constants.REL_SELF);
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot build self link", 500);
        }
    }

    private static void prepareAlternateLink(Entry entry, String href, String mimeType, String title) throws ResponseContextException {
        try {
            Link alternateLink = entry.addLink(entry.getId().toString());
            alternateLink.setHref(href + "?repr=" + mimeType);
            alternateLink.setMimeType(mimeType);
            alternateLink.setRel(Constants.REL_ALTERNATE);
            alternateLink.setTitle(title);
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot build alternate link", 500);
        }
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
                    String uriKey = getEntityID(uri);
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
                    String vocabulary = category.getScheme().toString();
                    String value = category.getTerm();
                    if (vocabulary != null && value != null) {
                        Subject subject = daoManager.getSubjectDao().getSubject(vocabulary, value);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                        }
                        subject.setTerm(vocabulary);
                        subject.setDefinedBy(value);
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

    public static Set<String> getUriKeysFromLink(Entry entry, String rel) throws ResponseContextException {
        Set<String> uriKeys = new HashSet<String>();
        try {
            List<Link> links = entry.getLinks(rel);
            for (Link link : links) {
                String id = getEntityID(link.getHref().toString());
                if (id != null) {
                    uriKeys.add(id);
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract href from link", 400);
        }
        return uriKeys;
    }

    private static Entry setCommonAttributes(Version version, boolean isParentLevel, String parentUrl) throws ResponseContextException {
        Abdera abdera = new Abdera();
        Entry entry;
        try {
            entry = abdera.newEntry();
            if (!isParentLevel) {
                //TODO this should accommodate external ids
                parentUrl = parentUrl + "/" + version.getUriKey();
            }
            entry.setId(parentUrl);
            //<link rel="http://www.openarchives.org/ore/terms/describes" href="http://dataspace.metadata.net/collections/2#"/>
            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            entry.setTitle(version.getTitle());
            entry.setContent(version.getDescription());
            entry.setUpdated(version.getUpdated());
            Date publishedDate = version.getParent().getPublishDate();
            if (publishedDate != null) {
                entry.setPublished(publishedDate);
            }
            Set<Agent> authors = version.getParent().getAuthors();
            for (Agent agent : authors) {
                AgentVersion workingCopy = (AgentVersion) agent.getWorkingCopy();
                entry.addAuthor(workingCopy.getTitle(), workingCopy.getMboxes().iterator().next(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey());
            }
            prepareSelfLink(entry, parentUrl);
            entry.addCategory(Constants.NS_ANDS_GROUP, Constants.TERM_ANDS_GROUP, Constants.TERM_ANDS_GROUP);
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to set mandatory attributes", 500);
        }
        return entry;
    }

    private static void addNavigationLinks(Version version, Entry entry, String parentUrl) throws ResponseContextException {
        try {
            entry.addLink(parentUrl, Constants.REL_LATEST_VERSION);
            SortedSet<Version> versions = version.getParent().getVersions();
            Version[] versionArray = new Version[versions.size()];
            versionArray = (Version[]) version.getParent().getVersions().toArray(versionArray);
            Version successorVersion = null;
            Version predecessorVersion = null;
            for (int i = 0; i < versionArray.length; i++) {
                if (versionArray[i].equals(version)) {
                    if (i > 0) {
                        successorVersion = versionArray[i - 1];
                    }
                    if (i < (versionArray.length - 1)) {
                        predecessorVersion = versionArray[i + 1];
                    }
                }
            }
            if (predecessorVersion != null) {
                entry.addLink(parentUrl + "/" + predecessorVersion.getUriKey(), Constants.REL_PREDECESSOR_VERSION);
            }
            if (successorVersion != null) {
                entry.addLink(parentUrl + "/" + successorVersion.getUriKey(), Constants.REL_SUCCESSOR_VERSION);
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to add link elements to entry", 400);
        }
    }

    private static void addContributor(Entry entry) throws ResponseContextException {
        try {
            entry.addContributor(Constants.UQ_REGISTRY_TITLE, Constants.UQ_REGISTRY_EMAIL, Constants.UQ_REGISTRY_URI_PREFIX);
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to add contributor", 500);
        }
    }

}
