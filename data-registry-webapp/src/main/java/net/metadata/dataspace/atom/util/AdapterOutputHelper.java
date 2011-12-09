package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Mbox;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.SourceAuthor;
import net.metadata.dataspace.data.model.context.Spatial;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.*;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.*;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
@Transactional
public class AdapterOutputHelper {

	private final Logger logger = Logger.getLogger(AdapterOutputHelper.class);

	private final FeedOutputHelper feedOutputHelper = new FeedOutputHelper();

    public Entry getEntryFromEntity(Version<?> version, boolean isParentLevel) throws ResponseContextException {
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

    public Entry getEntryFromActivity(ActivityVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();

        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            String activityTypelabel = WordUtils.capitalize(version.getType().toString().toLowerCase());
            if (activityTypelabel.equals("Project")) {
                Link typeLink = entry.addLink(Constants.NS_FOAF + activityTypelabel, Constants.REL_TYPE);
                typeLink.setTitle(activityTypelabel);
            } else {
                Link typeLink = entry.addLink(Constants.NS_VIVO + activityTypelabel, Constants.REL_TYPE);
                typeLink.setTitle(activityTypelabel);
            }

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

            Set<Agent> agentSet = version.getHasParticipants();
            for (Agent agent : agentSet) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_HAS_PARTICIPANT);
                link.setTitle(agent.getTitle());
            }
            Set<Collection> collectionSet = version.getHasOutput();
            for (Collection collection : collectionSet) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_HAS_OUTPUT);
                link.setTitle(collection.getTitle());
            }

            //Temporal
            Set<String> temporals = version.getTemporals();
            for (String temporal : temporals) {
                Element temporalElement = entry.addExtension(Constants.QNAME_RDFA_META);
                temporalElement.setAttributeValue("property", Constants.REL_TEMPORAL);
                temporalElement.setAttributeValue("content", temporal);
            }

            Set<Subject> subjectSet = version.getSubjects();
            addSubjectToEntry(entry, subjectSet);
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
        feedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public Entry getEntryFromAgent(AgentVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_AGENTS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);

        try {
            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            String agentTypelabel = WordUtils.capitalize(version.getType().toString().toLowerCase());
            Link typeLink = entry.addLink(Constants.NS_FOAF + agentTypelabel, Constants.REL_TYPE);
            typeLink.setTitle(agentTypelabel);

            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }

            FullName fullName = version.getParent().getFullName();
            if (fullName != null) {
            	if (fullName.getTitle() != null) {
	                Element fullNameTitle = entry.addExtension(Constants.QNAME_RDFA_META);
	                fullNameTitle.setAttributeValue("property", Constants.PROPERTY_TITLE);
	                fullNameTitle.setAttributeValue("content", fullName.getTitle());
            	}
                Element givenNameElement = entry.addExtension(Constants.QNAME_RDFA_META);
                givenNameElement.setAttributeValue("property", Constants.PROPERTY_GIVEN_NAME);
                givenNameElement.setAttributeValue("content", fullName.getGivenName());
                Element familyNameElement = entry.addExtension(Constants.QNAME_RDFA_META);
                familyNameElement.setAttributeValue("property", Constants.PROPERTY_FAMILY_NAME);
                familyNameElement.setAttributeValue("content", fullName.getFamilyName());
            }

            Set<Mbox> mboxes = version.getMboxes();
            for (Mbox mbox : mboxes) {
                Link link = entry.addLink(mbox.toUri().toString(),
                		Constants.REL_MBOX);
                link.setTitle(mbox.getEmailAddress());
            }

            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }

            //Add agent publications
            Set<Publication> publications = version.getPublications();
            for (Publication publication : publications) {
                String href = publication.getPublicationURI();
                Link link = entry.addLink(href, Constants.REL_PUBLICATIONS);
                link.setTitle(publication.getTitle());
            }


            Set<Collection> collectionSet = version.getMade();
            for (Collection collection : collectionSet) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_MADE);
                link.setTitle(collection.getTitle());
            }

            Set<Collection> collections = version.getIsManagerOf();
            for (Collection collection : collections) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_MANAGER_OF);
                link.setTitle(collection.getTitle());
            }

            Set<Service> services = version.getManagedServices();
            for (Service service : services) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_MANAGES_SERVICE);
                link.setTitle(service.getTitle());
            }

            Set<Activity> activities = version.getCurrentProjects();
            for (AbstractRecordEntity<ActivityVersion> activity : activities) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_CURRENT_PROJECT);
                link.setTitle(activity.getTitle());
            }

            Set<Subject> subjectSet = version.getSubjects();
            addSubjectToEntry(entry, subjectSet);

        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
        feedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public Entry getEntryFromCollection(CollectionVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {

            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            Set<Agent> authors = version.getCreators();
            for (Agent agent : authors) {
                AgentVersion workingCopy = agent.getWorkingCopy();
                Mbox mbox = workingCopy.getMboxes().size() > 0 ?
                		workingCopy.getMboxes().iterator().next() : null;
                entry.addAuthor(workingCopy.getTitle(), mbox.getEmailAddress(),
                		RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#");
            }

            String collectionTypeLabel = WordUtils.capitalize(version.getType().toString().toLowerCase());
            Link typeLink = entry.addLink(Constants.NS_DCMITYPE + collectionTypeLabel, Constants.REL_TYPE);
            typeLink.setTitle(collectionTypeLabel);

            Set<String> alternatives = version.getAlternatives();
            for (String alternativeName : alternatives) {
                Element alternativeElement = entry.addExtension(Constants.QNAME_RDFA_META);
                alternativeElement.setAttributeValue("property", Constants.REL_ALTERNATIVE);
                alternativeElement.setAttributeValue("content", alternativeName);
            }

            Set<Mbox> mboxes = version.getMboxes();
            for (Mbox mbox : mboxes) {
                String href = mbox.toUri().toString();
                Link link = entry.addLink(href, Constants.REL_MBOX);
                link.setTitle(mbox.getEmailAddress());
            }

            //Pages
            Set<String> pages = version.getPages();
            for (String page : pages) {
                entry.addLink(page, Constants.REL_PAGE);
            }
            //Subjects
            Set<Subject> subjectSet = version.getSubjects();
            addSubjectToEntry(entry, subjectSet);

            //Publishers
            Set<Agent> publishers = version.getPublishers();
            for (Agent publisher : publishers) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_AGENTS + "/" + publisher.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_PUBLISHER);
                link.setTitle(publisher.getTitle());
            }
            //Is Output of
            Set<Activity> activities = version.getOutputOf();
            for (AbstractRecordEntity<ActivityVersion> activity : activities) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_OUTPUT_OF);
                link.setTitle(activity.getTitle());
            }
            //Accessed via
            Set<Service> services = version.getAccessedVia();
            for (Service service : services) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_ACCESSED_VIA);
                link.setTitle(service.getTitle());
            }

            Set<Collection> collections = version.getRelations();
            for (Collection collection : collections) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_RELATED);
                link.setTitle(collection.getTitle());
            }

            //Publications
            Set<Publication> publications = version.getReferencedBy();
            for (Publication publication : publications) {
                String href = publication.getPublicationURI();
                Link link = entry.addLink(href, Constants.REL_IS_REFERENCED_BY);
                link.setTitle(publication.getTitle());
            }
            //Rights
            String rights = version.getRights();
            if (rights != null) {
                entry.setRights(rights);
            }

            String license = version.getLicense();
            if (license != null) {
                Link link = entry.addLink(license, Constants.REL_LICENSE);
            }
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
            //Spatial Coverage links
            for (Spatial spatial : version.getSpatialCoverage()) {
            	Link link = entry.addLink(spatial.getLocation().toString(),
            			Constants.REL_SPATIAL);
                link.setTitle(spatial.getName());
            }
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
        feedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public Entry getEntryFromService(ServiceVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addLink(parentUrl + "#", Constants.REL_DESCRIBES);
            String serviceTypelabel = WordUtils.capitalize(version.getType().toString().toLowerCase());
            Link typeLink = entry.addLink(Constants.NS_EFS + serviceTypelabel, Constants.REL_TYPE);
            typeLink.setTitle(serviceTypelabel);

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
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_SUPPORTED_BY);
                link.setTitle(collection.getTitle());
            }

            Set<Agent> agents = version.getManagedBy();
            for (Agent agent : agents) {
                String href = RegistryApplication.getApplicationContext().getUriPrefix() + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey() + "#";
                Link link = entry.addLink(href, Constants.REL_IS_MANAGED_BY);
                link.setTitle(agent.getTitle());
            }

        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
        feedOutputHelper.setPublished(version, entry);
        addSource(version, entry);
        addNavigationLinks(version, entry, parentUrl);
        return entry;
    }

    public ResponseContext getContextResponseForGetEntry(RequestContext request, Entry entry, Class<?> clazz) throws ResponseContextException {

        String accept = OperationHelper.getAcceptHeader(request);
        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        String selfLinkHref = entry.getLink(Constants.REL_SELF).getHref().toString();
        responseContext.setLocation(selfLinkHref);
        responseContext.setHeader("Vary", "Accept");
        if (accept.equals(Constants.MIME_TYPE_ATOM_ENTRY) || accept.equals(Constants.MIME_TYPE_ATOM)) {
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            responseContext.setContentType(Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            PrettyWriter writer = new PrettyWriter();
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RDF)) {
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RDF);
            String xslFilePath = "/files/xslt/rdf/atom2rdf-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_HTML)) {
            String viewRepresentation = OperationHelper.getViewRepresentation(request);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
//            if (request.getHeader("user-agent").toString().indexOf("MSIE ") > -1) {
            responseContext.setContentType(Constants.MIME_TYPE_HTML);
//            } else {
//                responseContext.setContentType(Constants.MIME_TYPE_XHTML);
//            }
            String xslFilePath = "/files/xslt/xhtml/atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            if (viewRepresentation != null && viewRepresentation.equals("edit")) {
                xslFilePath = "/files/xslt/xhtml/edit/edit-atom2xhtml-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            }
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath, request);
            responseContext.setWriter(writer);
        } else if (accept.equals(Constants.MIME_TYPE_RIFCS)) {
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_ATOM_ENTRY, Constants.MIM_TYPE_NAME_ATOM);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            responseContext.setContentType(Constants.MIME_TYPE_RIFCS);
            String xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + clazz.getSimpleName().toLowerCase() + ".xsl";
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            responseContext.setWriter(writer);
        } else {
            return OperationHelper.createResponse(415, Constants.HTTP_STATUS_415);
        }

        return responseContext;
    }

    public ResponseContext getContextResponseForPost(Entry entry) throws ResponseContextException {
        try {
            String selfLinkHref = entry.getLink(Constants.REL_SELF).getHref().toString();
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            ResponseContext responseContext = ProviderHelper.returnBase(entry, 201, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
            responseContext.setEntityTag(ProviderHelper.calculateEntityTag(entry));
            responseContext.setLocation(entry.getLink(Constants.REL_SELF).getHref().toString());
            return responseContext;
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
    }

    public ResponseContext getContextResponseForPut(Entry entry) throws ResponseContextException {
        try {
            String selfLinkHref = entry.getLink(Constants.REL_SELF).getHref().toString();
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_XHTML, Constants.MIM_TYPE_NAME_XHTML);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RDF, Constants.MIM_TYPE_NAME_RDF);
            prepareAlternateLink(entry, selfLinkHref, Constants.MIME_TYPE_RIFCS, Constants.MIM_TYPE_NAME_RIFCS);
            ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
            responseContext.setEntityTag(ProviderHelper.calculateEntityTag(entry));
            responseContext.setLocation(entry.getLink(Constants.REL_SELF).getHref().toString());
            return responseContext;
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException(500, th);
        }
    }

    private void prepareSelfLink(Entry entry, String href, String title) throws ResponseContextException {
        try {
            Link selfLink = entry.getSelfLink();
            if (selfLink == null) {
                selfLink = entry.addLink(entry.getId().toString());
            }
            selfLink.setHref(href);
            selfLink.setRel(Constants.REL_SELF);
            selfLink.setTitle(title);
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Cannot build self link", 500);
        }
    }

    private void prepareAlternateLink(Entry entry, String href, String mimeType, String title) throws ResponseContextException {
        try {
            Link alternateLink = entry.addLink(entry.getId().toString());
            alternateLink.setHref(href + "?repr=" + mimeType);
            alternateLink.setMimeType(mimeType);
            alternateLink.setRel(Constants.REL_ALTERNATE);
            alternateLink.setTitle(title);
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Cannot build alternate link", 500);
        }
    }

    private Entry setCommonAttributes(Version version, boolean isParentLevel, String parentUrl) throws ResponseContextException {
        Abdera abdera = new Abdera();
        Entry entry;
        try {
            entry = abdera.newEntry();
            if (!isParentLevel) {
                //TODO this should accommodate external ids
                parentUrl = parentUrl + "/" + version.getUriKey();
            }
            if (version.getParent().getOriginalId() != null) {
                entry.setId(version.getParent().getOriginalId());
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
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Failed to set mandatory attributes", 500);
        }
        return entry;
    }

    private void addNavigationLinks(Version version, Entry entry, String parentUrl) throws ResponseContextException {
        try {
            if (version.getParent().getWorkingCopy() != null) {
	            String workingCopyKey = version.getParent().getWorkingCopy().getUriKey();
	            Link link = entry.addLink(parentUrl + "/" + workingCopyKey, Constants.REL_WORKING_COPY);
	            link.setTitle(workingCopyKey);
            }
            if (version.getParent().getPublished() != null) {
	            String publishedCopyKey = version.getParent().getPublished().getUriKey();
	            Link link = entry.addLink(parentUrl + "/" + publishedCopyKey, Constants.REL_LATEST_VERSION);
	            link.setTitle(publishedCopyKey);
            }
            // Edit relation is always the parent entity
	        entry.addLink(parentUrl, Constants.REL_EDIT);
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
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Failed to add link elements to entry", 400);
        }
    }

    private void addSource(Version<?> version, Entry entry) throws ResponseContextException {
        try {
            Source source = Abdera.getNewFactory().newSource(entry);
            net.metadata.dataspace.data.model.context.Source registrySource = version.getSource();
            if (registrySource != null) {
                source.setId(registrySource.getSourceURI());
                source.setTitle(registrySource.getTitle());
            } else {
                source.setId(RegistryApplication.getApplicationContext().getUriPrefix());
                source.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle());
            }
            logger.debug("Adding description authors: "+version.getDescriptionAuthors());
            for (SourceAuthor descriptionAuthor : version.getDescriptionAuthors()) {
            	Person person = source.addAuthor(descriptionAuthor.getName());
            	person.setEmail(descriptionAuthor.getEmail());
            	if (descriptionAuthor.getUri() != null)
            		person.setUri(descriptionAuthor.getUri().toString());
            }
            Link publisher = source.addLink(Constants.UQ_URL, Constants.REL_PUBLISHER);
            publisher.setTitle(Constants.TERM_ANDS_GROUP);
            source.setRights(version.getParent().getSourceRights());
            source.addLink(version.getParent().getSourceLicense(), Constants.REL_LICENSE);
        } catch (Throwable th) {
        	logger.warn(th.getMessage(), th);
            throw new ResponseContextException("Failed to add source", 500);
        }
    }

    private void addSubjectToEntry(Entry entry, Set<Subject> subjectSet) {
        for (Subject sub : subjectSet) {
            if (sub.getLabel().equals(Constants.LABEL_KEYWORD)) {
                entry.addCategory(sub.getTerm());
            } else {
                entry.addCategory(sub.getDefinedBy(), sub.getTerm(), sub.getLabel());
            }
        }
    }

}
