package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.record.resource.Subject;
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
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

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
            } else if (version instanceof ServiceVersion) {
                //TODO this need to be retrieved from the entry
                ((ServiceVersion) version).setType(ServiceType.SYNDICATE);
            }
        }
    }

    private static Entry getEntryFromActivity(ActivityVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        entry.addCategory(Constants.SCHEME_FOAF, Constants.TERM_ACTIVITY, version.getParent().getClass().getSimpleName());
        try {
            Set<Agent> agentSet = version.getHasParticipant();
            for (Agent agent : agentSet) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                entry.addLink(href, Constants.REL_HAS_PARTICIPANT);
            }
            Set<Collection> collectionSet = version.getHasOutput();
            for (Collection collection : collectionSet) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                entry.addLink(href, Constants.REL_HAS_OUTPUT);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromAgent(AgentVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        entry.addCategory(Constants.SCHEME_FOAF, Constants.TERM_AGENT_AS_AGENT, version.getParent().getClass().getSimpleName());
        try {
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getVocabulary(), sub.getValue(), sub.getLabel());
            }

            Set<Collection> collectionSet = version.getCollectorOf();
            for (Collection collection : collectionSet) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                entry.addLink(href, Constants.REL_IS_COLLECTOR_OF);
            }
            Set<Activity> activities = version.getParticipantIn();
            for (Activity activity : activities) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                entry.addLink(href, Constants.REL_IS_PARTICIPANT_IN);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromCollection(CollectionVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
        //<category scheme="http://purl.org/dc/dcmitype/" term="http://purl.org/dc/dcmitype/Collection" label="Collection"/>
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        entry.addCategory(Constants.SCHEME_DCMITYPE, Constants.TERM_COLLECTION, version.getParent().getClass().getSimpleName());
        try {
            entry.addLink(version.getLocation(), Constants.REL_IS_LOCATED_AT);
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getVocabulary(), sub.getValue(), sub.getLabel());
            }
            Set<Agent> agents = version.getCollector();
            for (Agent agent : agents) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                entry.addLink(href, Constants.REL_CREATOR);
            }
            Set<Service> services = version.getSupports();
            for (Service service : services) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey() + "#";
                entry.addLink(href, Constants.REL_IS_ACCESSED_VIA);
            }
            Set<Activity> activities = version.getOutputOf();
            for (Activity activity : activities) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                entry.addLink(href, Constants.REL_IS_OUTPUT_OF);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromService(ServiceVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        entry.addCategory(Constants.SCHEME_VIVO, Constants.TERM_SERVICE, version.getParent().getClass().getSimpleName());
        try {
            entry.addLink(version.getLocation(), Constants.REL_IS_LOCATED_AT);
            Set<Collection> collectionSet = version.getSupportedBy();
            for (Collection collection : collectionSet) {
                String href = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                entry.addLink(href, Constants.REL_IS_SUPPORTED_BY);
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedHelper.setPublished(version, entry);
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
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS);
            responseContext.setContentType(Constants.MIME_TYPE_ATOM_ENTRY);
            PrettyWriter writer = new PrettyWriter();
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RDF)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RDF);
            String xslFilePath = "/files/xslt/rdf/atom2rdf-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_XHTML)) {
            String selfLinkHref = entry.getId().toString();
            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF);
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
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML);
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

    private static void prepareAlternateLink(Entry entry, String href, String mimeType) throws ResponseContextException {
        try {
            Link alternateLink = entry.addLink(entry.getId().toString());
            alternateLink.setHref(href + "?repr=" + mimeType);
            alternateLink.setMimeType(mimeType);
            alternateLink.setRel(Constants.REL_ALTERNATE);
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot build alternate link", 500);
        }
    }

    public static boolean isValidVersionFromEntry(Version version, Entry entry) throws ResponseContextException {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            String content = entry.getContent();
            if (content == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
            }
            try {
                version.setTitle(entry.getTitle());
                version.setDescription(content);
                version.setUpdated(entry.getUpdated());
                version.setAuthors(getAuthors(entry.getAuthors()));
            } catch (Throwable th) {
                throw new ResponseContextException(500, th);
            }
            addType(version, entry);
            if (version instanceof CollectionVersion || version instanceof ServiceVersion) {
                addLocation(version, entry);
            }
            return true;
        }
    }

    private static void addLocation(Version version, Entry entry) throws ResponseContextException {
        try {
            Link link = entry.getLink(Constants.REL_IS_LOCATED_AT);
            String location = link.getHref().toString();
            version.setLocation(location);
        } catch (Throwable th) {
            throw new ResponseContextException(400, th);
        }
    }

    private static Set<String> getAuthors(List<Person> persons) throws ResponseContextException {
        Set<String> authors = new HashSet<String>();
        try {
            for (Person person : persons) {
                authors.add(person.getName());
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract authors", 500);
        }
        return authors;
    }

    public static Set<Subject> getSubjects(Entry entry) throws ResponseContextException {
        Set<Subject> subjects = new HashSet<Subject>();
        try {
            List<Category> categories = entry.getCategories();
            for (Category category : categories) {
                if (!category.getScheme().toString().equals(Constants.SCHEME_DCMITYPE)) {
                    String vocabulary = category.getScheme().toString();
                    String value = category.getTerm();
                    if (vocabulary != null && value != null) {
                        Subject subject = daoManager.getSubjectDao().getSubject(vocabulary, value);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                        }
                        subject.setVocabulary(vocabulary);
                        subject.setValue(value);
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
            Set<String> authors = version.getAuthors();
            for (String author : authors) {
                entry.addAuthor(author);
            }
            prepareSelfLink(entry, parentUrl);
            entry.addCategory(Constants.SCHEME_ANDS_GROUP, Constants.TERM_ANDS_GROUP, Constants.TERM_ANDS_GROUP);
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

}
