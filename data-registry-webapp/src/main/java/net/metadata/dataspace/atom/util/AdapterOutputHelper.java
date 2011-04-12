package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Source;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
public class AdapterOutputHelper {

    private static Logger logger = Logger.getLogger(AdapterOutputHelper.class);
    private static final EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
    private static DaoManager daoManager = RegistryApplication.getApplicationContext().getDaoManager();


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

    private static Entry getEntryFromActivity(ActivityVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();

        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_FOAF, Constants.TERM_ACTIVITY, version.getType().toString());
            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }

            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }

            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getDefinedBy(), sub.getTerm(), sub.getLabel());
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
        FeedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromAgent(AgentVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);

        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_FOAF, Constants.TERM_AGENT_AS_AGENT, version.getType().toString());
            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }

            Set<String> mboxes = version.getMboxes();
            for (String mbox : mboxes) {
                String href = "mailto:" + mbox;
                Link link = entry.addLink(href, Constants.REL_MBOX);
                link.setTitle(mbox);
            }

            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getDefinedBy(), sub.getTerm(), sub.getLabel());
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
        FeedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromCollection(CollectionVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {

            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            Set<Agent> authors = version.getCreators();
            for (Agent agent : authors) {
                AgentVersion workingCopy = agent.getWorkingCopy();
                entry.addAuthor(workingCopy.getTitle(), workingCopy.getMboxes().iterator().next(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey());
            }

            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_DCMITYPE, Constants.TERM_COLLECTION, version.getType().toString());

            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }
            //Pages
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            //Subjects
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                entry.addCategory(sub.getDefinedBy(), sub.getTerm(), sub.getLabel());
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
            Set<String> geoRssPolygons = version.getGeoRssPolygons();
            for (String geoRssPolygon : geoRssPolygons) {
                entry.addSimpleExtension(Constants.QNAME_GEO_RSS_POLYGON, geoRssPolygon);
            }
            //GeoRss Feature Names
            Set<String> geoRssFeatureNames = version.getGeoRssFeatureNames();
            for (String geoRssFeatureName : geoRssFeatureNames) {
                //TODO need to add the href in the data model
                Link link = entry.addLink("", Constants.REL_SPATIAL);
                link.setTitle(geoRssFeatureName);
            }


        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        FeedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromService(ServiceVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl, Constants.REL_IS_DESCRIBED_BY);
            entry.addCategory(Constants.NS_VIVO, Constants.TERM_SERVICE, version.getType().toString());
            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }
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
        FeedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public static ResponseContext getContextResponseForGetEntry(RequestContext request, Entry entry, Class clazz) throws ResponseContextException {

        String accept = OperationHelper.getAcceptHeader(request);
        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        responseContext.setLocation(entry.getId().toString());
        responseContext.setHeader("Vary", "Accept");
        if (accept.equals(Constants.MIME_TYPE_ATOM_ENTRY) || accept.equals(Constants.MIME_TYPE_ATOM)) {
            String selfLinkHref = entry.getId().toString();
//            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            responseContext.setContentType(Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            PrettyWriter writer = new PrettyWriter();
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RDF)) {
            String selfLinkHref = entry.getId().toString();
//            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RDF);
            String xslFilePath = "/files/xslt/rdf/atom2rdf-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_XHTML)) {
            String viewRepresentation = OperationHelper.getViewRepresentation(request);
            String selfLinkHref = entry.getId().toString();
//            prepareSelfLink(entry, selfLinkHref);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            if (request.getHeader("user-agent").toString().indexOf("MSIE ") > -1) {
                responseContext.setContentType(Constants.MIME_TYPE_HTML);
            } else {
                responseContext.setContentType(Constants.MIME_TYPE_XHTML);
            }
            String xslFilePath = "/files/xslt/xhtml/atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            if (viewRepresentation != null && viewRepresentation.equals("edit")) {
                xslFilePath = "/files/xslt/xhtml/edit/edit-atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            }
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath, request);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RIFCS)) {
            String selfLinkHref = entry.getId().toString();
//            prepareSelfLink(entry, selfLinkHref);
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

    private static void prepareSelfLink(Entry entry, String href, String title) throws ResponseContextException {
        try {
            Link selfLink = entry.getSelfLink();
            if (selfLink == null) {
                selfLink = entry.addLink(entry.getId().toString());
            }
            selfLink.setHref(href);
            selfLink.setRel(Constants.REL_SELF);
            selfLink.setTitle(title);
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

    private static Entry setCommonAttributes(Version version, boolean isParentLevel, String parentUrl) throws ResponseContextException {
        Abdera abdera = new Abdera();
        Entry entry;
        try {
            entry = abdera.newEntry();
            if (!isParentLevel) {
                //TODO this should accommodate external ids
                parentUrl = parentUrl + "/" + version.getUriKey();
            }
            if (version instanceof CollectionVersion && ((CollectionVersion) version).getOriginalId() != null) {
                entry.setId(((CollectionVersion) version).getOriginalId());
            } else {
                entry.setId(parentUrl);
            }
            entry.setTitle(version.getTitle());
            entry.setContent(version.getDescription());
            String uriKey = version.getParent().getUriKey();
            prepareSelfLink(entry, parentUrl, uriKey);
            entry.setUpdated(version.getUpdated());
            Date publishedDate = version.getParent().getCreated();
            if (publishedDate != null) {
                entry.setPublished(publishedDate);
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to set mandatory attributes", 500);
        }
        return entry;
    }

    private static void addNavigationLinks(Version version, Entry entry, String parentUrl) throws ResponseContextException {
        try {
            String workingCopyKey = version.getParent().getWorkingCopy().getUriKey();
            Link link = entry.addLink(parentUrl + "/" + workingCopyKey, Constants.REL_LATEST_VERSION);
            link.setTitle(workingCopyKey);
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

    private static void addSource(Version version, Entry entry) throws ResponseContextException {
        try {
            Source source = Abdera.getNewFactory().newSource(entry);
            net.metadata.dataspace.data.model.context.Source registrySource = version.getParent().getSource();
            if (registrySource != null) {
                source.setId(registrySource.getSourceURI());
                source.setTitle(registrySource.getTitle());
            }
            Set<Agent> authors = version.getParent().getAuthors();
            for (Agent author : authors) {
                source.addAuthor(author.getTitle(), author.getMBoxes().iterator().next(), Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey());
            }
            source.addLink(Constants.UQ_URL, Constants.REL_PUBLISHER);
            source.setRights(Constants.UQ_REGISTRY_RIGHTS);
            source.addLink(Constants.REL_LICENSE, Constants.UQ_REGISTRY_LICENSE);
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to add contributor", 500);
        }
    }

}
