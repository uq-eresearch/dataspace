package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.base.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
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

import javax.xml.namespace.QName;
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
            } else if (version instanceof PartyVersion) {
                return getEntryFromParty((PartyVersion) version, isParentLevel);
            } else if (version instanceof CollectionVersion) {
                return getEntryFromCollection((CollectionVersion) version, isParentLevel);
            } else if (version instanceof ServiceVersion) {
                return getEntryFromService((ServiceVersion) version, isParentLevel);
            }
        }
        return null;
    }

    private static Entry getEntryFromActivity(ActivityVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            Set<Party> partySet = version.getHasParticipant();
            for (Party sub : partySet) {
                Element partyElement = entry.addExtension(Constants.QNAME_HAS_PARTICIPANT);
                partyElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + sub.getUriKey());
            }
            Set<Collection> collectionSet = version.getHasOutput();
            for (Collection collection : collectionSet) {
                Element collectorOfElement = entry.addExtension(Constants.QNAME_HAS_OUTPUT);
                collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        setPublished(version, entry);
        addLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromParty(PartyVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                Element subjectElement = entry.addExtension(Constants.QNAME_SUBJECT);
                subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VOCABULARY, sub.getVocabulary());
                subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VALUE, sub.getValue());
            }
            Set<Collection> collectionSet = version.getCollectorOf();
            for (Collection collection : collectionSet) {
                Element collectorOfElement = entry.addExtension(Constants.QNAME_COLLECTOR_OF);
                collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
            }
            Set<Activity> activities = version.getParticipantIn();
            for (Activity activity : activities) {
                Element serviceElement = entry.addExtension(Constants.QNAME_IS_PARTICIPANT_IN);
                serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        setPublished(version, entry);
        addLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromCollection(CollectionVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addSimpleExtension(Constants.QNAME_LOCATION, version.getLocation());
            Set<Subject> subjectSet = version.getSubjects();
            for (Subject sub : subjectSet) {
                Element subjectElement = entry.addExtension(Constants.QNAME_SUBJECT);
                subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VOCABULARY, sub.getVocabulary());
                subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VALUE, sub.getValue());
            }
            Set<Party> parties = version.getCollector();
            for (Party party : parties) {
                Element partyElement = entry.addExtension(Constants.QNAME_COLLECTOR);
                partyElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + party.getUriKey());
            }
            Set<Service> services = version.getSupports();
            for (Service service : services) {
                Element serviceElement = entry.addExtension(Constants.QNAME_SUPPORTS);
                serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey());
            }
            Set<Activity> activities = version.getOutputOf();
            for (Activity activity : activities) {
                Element serviceElement = entry.addExtension(Constants.QNAME_IS_OUTPUT_OF);
                serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        setPublished(version, entry);
        addLinks(version, entry, parentUrl);
        return entry;
    }

    private static Entry getEntryFromService(ServiceVersion version, boolean isParentLevel) throws ResponseContextException {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + version.getParent().getUriKey();
        Entry entry = setCommonAttributes(version, isParentLevel, parentUrl);
        try {
            entry.addSimpleExtension(Constants.QNAME_LOCATION, version.getLocation());
            Set<Collection> collectionSet = version.getSupportedBy();
            for (Collection collection : collectionSet) {
                Element collectorOfElement = entry.addExtension(Constants.QNAME_SUPPORTED_BY);
                collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
            }
        } catch (Throwable th) {
            throw new ResponseContextException(500, th);
        }
        setPublished(version, entry);
        addLinks(version, entry, parentUrl);
        return entry;
    }

    private static void setPublished(Version version, Entry entry) {
        Control control = entry.addControl();
        Version published = version.getParent().getPublished();
        //False is used her to indicate the version is published and true (isDraft) is not published
        if (published != null && version.equals(published)) {
            control.setDraft(false);
        } else {
            control.setDraft(true);
        }
    }

    public static ResponseContext getContextResponseForGetEntry(RequestContext request, Entry entry) throws ResponseContextException {

        String representationMimeType = AdapterHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader != null) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.ATOM_ENTRY_MIMETYPE;
            }
        }

        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        responseContext.setLocation(entry.getId().toString());
        responseContext.setHeader("Vary", "Accept");
        /*if (representationMimeType.equals(Constants.JSON_MIMETYPE)) {
            String selfLinkHref = entry.getId() + "?repr=" + Constants.JSON_MIMETYPE;
            prepareSelfLink(entry, selfLinkHref, Constants.JSON_MIMETYPE);

            String alternateLinkHref = entry.getId() + "?repr=" + Constants.ATOM_ENTRY_MIMETYPE;
            prepareAlternateLink(entry, alternateLinkHref, Constants.ATOM_ENTRY_MIMETYPE);

            responseContext.setContentType(Constants.JSON_MIMETYPE);
            responseContext.setWriter(new JSONWriter());
        } else*/
        if (representationMimeType.equals(Constants.ATOM_ENTRY_MIMETYPE) || representationMimeType.equals(Constants.ATOM_MIMETYPE)) {
            String selfLinkHref = entry.getId() + "?repr=" + Constants.ATOM_ENTRY_MIMETYPE;
            prepareSelfLink(entry, selfLinkHref, Constants.ATOM_ENTRY_MIMETYPE);

//            String alternateLinkHref = entry.getId() + "?repr=" + Constants.JSON_MIMETYPE;
//            prepareAlternateLink(entry, alternateLinkHref, Constants.JSON_MIMETYPE);

            responseContext.setContentType(Constants.ATOM_ENTRY_MIMETYPE);
            responseContext.setWriter(new PrettyWriter());
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

    private static void prepareSelfLink(Entry entry, String href, String mimeType) throws ResponseContextException {
        try {
            Link selfLink = entry.getSelfLink();
            if (selfLink == null) {
                selfLink = entry.addLink(entry.getId().toString());
            }
            selfLink.setHref(href);
            selfLink.setMimeType(mimeType);
            selfLink.setRel(Constants.REL_TYPE_SELF);
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot build self link", 500);
        }
    }

    private static void prepareAlternateLink(Entry entry, String href, String mimeType) throws ResponseContextException {
        try {
            Link alternateLink = entry.getAlternateLink();
            if (alternateLink == null) {
                alternateLink = entry.addLink(entry.getId().toString());
            }
            alternateLink.setHref(href);
            alternateLink.setMimeType(mimeType);
            alternateLink.setRel(Constants.REL_TYPE_ALTERNATE);
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot build alternate link", 500);
        }
    }

    public static boolean isValidVersionFromEntry(Version version, Entry entry) throws ResponseContextException {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            String summary = entry.getSummary();
            String content = entry.getContent();
            if (summary == null || content == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
            }
            try {
                version.setTitle(entry.getTitle());
                version.setSummary(summary);
                version.setContent(content);
                version.setUpdated(entry.getUpdated());
                version.setAuthors(getAuthors(entry.getAuthors()));
            } catch (Throwable th) {
                throw new ResponseContextException(500, th);
            }
            if (version instanceof CollectionVersion || version instanceof ServiceVersion) {
                addLocation(version, entry);
            }
            return true;
        }
    }

    private static void addLocation(Version version, Entry entry) throws ResponseContextException {
        try {
            List<Element> extensions = entry.getExtensions();
            for (Element extension : extensions) {
                if (extension.getQName().equals(Constants.QNAME_LOCATION)) {
                    String location = extension.getText();
                    version.setLocation(location);
                }
            }
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
            List<Element> extensionElements = entry.getExtensions();
            for (Element extension : extensionElements) {
                if (extension.getQName().equals(Constants.QNAME_SUBJECT)) {
                    String vocabulary = extension.getAttributeValue(Constants.ATTRIBUTE_NAME_VOCABULARY);
                    String value = extension.getAttributeValue(Constants.ATTRIBUTE_NAME_VALUE);
                    if (vocabulary != null && value != null) {

                        Subject subject = daoManager.getSubjectDao().getSubject(vocabulary, value);
                        if (subject == null) {
                            subject = entityCreator.getNextSubject();
                        }
                        subject.setVocabulary(vocabulary);
                        subject.setValue(value);
                        subjects.add(subject);
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract subjects from entry", 400);
        }
        return subjects;
    }

    public static Set<String> getUriKeysFromExtension(Entry entry, QName qName) throws ResponseContextException {
        Set<String> uriKeys = new HashSet<String>();
        try {
            List<Element> extensionElements = entry.getExtensions();
            for (Element extension : extensionElements) {
                if (extension.getQName().equals(qName)) {
                    String id = getEntityID(extension.getAttributeValue(Constants.ATTRIBUTE_NAME_URI));
                    if (id != null) {
                        uriKeys.add(id);
                    }
                }
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Cannot extract " + Constants.ATTRIBUTE_NAME_URI + " from extension", 400);
        }
        return uriKeys;
    }

    private static Entry setCommonAttributes(Version version, boolean isParentLevel, String parentUrl) throws ResponseContextException {
        Abdera abdera = new Abdera();
        Entry entry;
        try {
            entry = abdera.newEntry();
            if (isParentLevel) {
                entry.setId(parentUrl);
            } else {
                entry.setId(parentUrl + "/" + version.getUriKey());
            }
            entry.setTitle(version.getTitle());
            entry.setSummary(version.getSummary());
            entry.setContent(version.getContent());
            entry.setUpdated(version.getUpdated());
            Set<String> authors = version.getAuthors();
            for (String author : authors) {
                entry.addAuthor(author);
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to set mandatory attributes", 500);
        }
        return entry;
    }

    private static void addLinks(Version version, Entry entry, String parentUrl) throws ResponseContextException {
        try {
            entry.addLink(parentUrl, Constants.REL_TYPE_LATEST_VERSION);
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
                entry.addLink(parentUrl + "/" + predecessorVersion.getUriKey(), Constants.REL_TYPE_PREDECESSOR_VERSION);
            }
            if (successorVersion != null) {
                entry.addLink(parentUrl + "/" + successorVersion.getUriKey(), Constants.REL_TYPE_SUCCESSOR_VERSION);
            }
        } catch (Throwable th) {
            throw new ResponseContextException("Failed to add link elements to entry", 400);
        }
    }

}
