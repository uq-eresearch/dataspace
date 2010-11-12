package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.json.JSONWriter;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String getEntryVersionID(RequestContext request) {
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

    public static String getJsonString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException ex) {
                logger.fatal("Could not parse inputstream to a JSON string", ex);
                return null;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.fatal("Could not close inputstream", ex);
                }
            }
        } else {
            return null;
        }
        String jsonString = sb.toString();
        return jsonString;
    }

    public static Entry getEntryFromActivity(ActivityVersion activityVersion, boolean isParentLevel) {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activityVersion.getParent().getUriKey();
        Entry entry = setCommonAttributes(activityVersion, isParentLevel, parentUrl);
        Set<Party> partySet = activityVersion.getHasParticipant();
        for (Party sub : partySet) {
            Element partyElement = entry.addExtension(Constants.QNAME_HAS_PARTICIPANT);
            partyElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + sub.getUriKey());
        }
        Set<Collection> collectionSet = activityVersion.getHasOutput();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.QNAME_HAS_OUTPUT);
            collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        }
        addLinks(activityVersion, entry, parentUrl);
        return entry;
    }

    public static Entry getEntryFromParty(PartyVersion partyVersion, boolean isParentLevel) {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + partyVersion.getParent().getUriKey();
        Entry entry = setCommonAttributes(partyVersion, isParentLevel, parentUrl);
        Set<Subject> subjectSet = partyVersion.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(Constants.QNAME_SUBJECT);
            subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VOCABULARY, sub.getVocabulary());
            subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VALUE, sub.getValue());
        }
        Set<Collection> collectionSet = partyVersion.getCollectorOf();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.QNAME_COLLECTOR_OF);
            collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        }
        Set<Activity> activities = partyVersion.getParticipantIn();
        for (Activity activity : activities) {
            Element serviceElement = entry.addExtension(Constants.QNAME_IS_PARTICIPANT_IN);
            serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
        }
        addLinks(partyVersion, entry, parentUrl);
        return entry;
    }

    public static Entry getEntryFromCollection(CollectionVersion collectionVersion, boolean isParentLevel) {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collectionVersion.getParent().getUriKey();
        Entry entry = setCommonAttributes(collectionVersion, isParentLevel, parentUrl);
        entry.addSimpleExtension(Constants.QNAME_LOCATION, collectionVersion.getLocation());
        Set<Subject> subjectSet = collectionVersion.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(Constants.QNAME_SUBJECT);
            subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VOCABULARY, sub.getVocabulary());
            subjectElement.setAttributeValue(Constants.ATTRIBUTE_NAME_VALUE, sub.getValue());
        }
        Set<Party> parties = collectionVersion.getCollector();
        for (Party party : parties) {
            Element partyElement = entry.addExtension(Constants.QNAME_COLLECTOR);
            partyElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + party.getUriKey());
        }
        Set<Service> services = collectionVersion.getSupports();
        for (Service service : services) {
            Element serviceElement = entry.addExtension(Constants.QNAME_SUPPORTS);
            serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + service.getUriKey());
        }
        Set<Activity> activities = collectionVersion.getOutputOf();
        for (Activity activity : activities) {
            Element serviceElement = entry.addExtension(Constants.QNAME_IS_OUTPUT_OF);
            serviceElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activity.getUriKey());
        }
        addLinks(collectionVersion, entry, parentUrl);
        return entry;
    }

    public static Entry getEntryFromService(ServiceVersion serviceVersion, boolean isParentLevel) {
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + serviceVersion.getParent().getUriKey();
        Entry entry = setCommonAttributes(serviceVersion, isParentLevel, parentUrl);
        entry.addSimpleExtension(Constants.QNAME_LOCATION, serviceVersion.getLocation());
        Set<Collection> collectionSet = serviceVersion.getSupportedBy();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.QNAME_SUPPORTED_BY);
            collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        }
        addLinks(serviceVersion, entry, parentUrl);
        return entry;
    }

    public static ResponseContext getContextResponseForGetEntry(RequestContext request, Entry entry) {

        String representationMimeType = AdapterHelper.getRepresentationMimeType(request);
        if (representationMimeType == null) {
            String acceptHeader = request.getAccept();
            if (acceptHeader.equals(Constants.JSON_MIMETYPE) || acceptHeader.equals(Constants.ATOM_ENTRY_MIMETYPE)) {
                representationMimeType = acceptHeader;
            } else {
                representationMimeType = Constants.ATOM_ENTRY_MIMETYPE;
            }
        }

        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, entry.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        responseContext.setLocation(entry.getId().toString());
        responseContext.setHeader("Vary", "Accept");
        if (representationMimeType.equals(Constants.JSON_MIMETYPE)) {
            String selfLinkHref = entry.getId() + "?repr=" + Constants.JSON_MIMETYPE;
            prepareSelfLink(entry, selfLinkHref, Constants.JSON_MIMETYPE);

            String alternateLinkHref = entry.getId() + "?repr=" + Constants.ATOM_ENTRY_MIMETYPE;
            prepareAlternateLink(entry, alternateLinkHref, Constants.ATOM_ENTRY_MIMETYPE);

            responseContext.setContentType(Constants.JSON_MIMETYPE);
            responseContext.setWriter(new JSONWriter());
        } else if (representationMimeType.equals(Constants.ATOM_ENTRY_MIMETYPE)) {
            String selfLinkHref = entry.getId() + "?repr=" + Constants.ATOM_ENTRY_MIMETYPE;
            prepareSelfLink(entry, selfLinkHref, Constants.ATOM_ENTRY_MIMETYPE);

            String alternateLinkHref = entry.getId() + "?repr=" + Constants.JSON_MIMETYPE;
            prepareAlternateLink(entry, alternateLinkHref, Constants.JSON_MIMETYPE);

            responseContext.setContentType(Constants.ATOM_ENTRY_MIMETYPE);
            responseContext.setWriter(new PrettyWriter());
        } else {
            return ProviderHelper.createErrorResponse(new Abdera(), 406, "The requested entry cannot be supplied in " + representationMimeType + " mime type.");
        }

        return responseContext;
    }

    public static ResponseContext getContextResponseForPost(Entry entry) {
        ResponseContext responseContext = ProviderHelper.returnBase(entry, 201, entry.getUpdated());
        responseContext.setEntityTag(ProviderHelper.calculateEntityTag(entry));
        responseContext.setLocation(entry.getId().toString());
        return responseContext;
    }

    private static void prepareSelfLink(Entry entry, String href, String mimeType) {
        Link selfLink = entry.getSelfLink();
        if (selfLink == null) {
            selfLink = entry.addLink(entry.getId().toString());
        }
        selfLink.setHref(href);
        selfLink.setMimeType(mimeType);
        selfLink.setRel(Constants.REL_TYPE_SELF);
    }

    private static void prepareAlternateLink(Entry entry, String href, String mimeType) {
        Link alternateLink = entry.getAlternateLink();
        if (alternateLink == null) {
            alternateLink = entry.addLink(entry.getId().toString());
        }
        alternateLink.setHref(href);
        alternateLink.setMimeType(mimeType);
        alternateLink.setRel(Constants.REL_TYPE_ALTERNATE);
    }

    public static boolean isValidVersionFromEntry(Version version, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            version.setTitle(entry.getTitle());
            version.setSummary(entry.getSummary());
            version.setContent(entry.getContent());
            version.setUpdated(entry.getUpdated());
            version.setAuthors(getAuthors(entry.getAuthors()));
            if (version instanceof CollectionVersion || version instanceof ServiceVersion) {
                addLocation(version, entry);
            }
            return true;
        }
    }

    private static void addLocation(Version version, Entry entry) {
        List<Element> extensions = entry.getExtensions();
        for (Element extension : extensions) {
            if (extension.getQName().equals(Constants.QNAME_LOCATION)) {
                String location = extension.getText();
                version.setLocation(location);
            }
        }
    }

    private static Set<String> getAuthors(List<Person> persons) {
        Set<String> authors = new HashSet<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }

    public static Set<Subject> getSubjects(Entry entry) {
        Set<Subject> subjects = new HashSet<Subject>();
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
        return subjects;
    }

    public static Set<String> getUriKeysFromExtension(Entry entry, QName qName) {
        Set<String> uriKeys = new HashSet<String>();
        List<Element> extensionElements = entry.getExtensions();
        for (Element extension : extensionElements) {
            if (extension.getQName().equals(qName)) {
                String id = getEntityID(extension.getAttributeValue(Constants.ATTRIBUTE_NAME_URI));
                if (id != null) {
                    uriKeys.add(id);
                }
            }
        }
        return uriKeys;
    }

    private static Entry setCommonAttributes(Version version, boolean isParentLevel, String parentUrl) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
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
        return entry;
    }

    private static void addLinks(Version version, Entry entry, String parentUrl) {
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
    }
}
