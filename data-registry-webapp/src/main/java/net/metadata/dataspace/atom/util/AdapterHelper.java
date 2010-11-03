package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.*;
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
    private static final EntityCreator entityCreator = DataRegistryApplication.getApplicationContext().getEntityCreator();
    private static DaoManager daoManager = DataRegistryApplication.getApplicationContext().getDaoManager();

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
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_ACTIVITIES + "/" + activityVersion.getParent().getUriKey();
        if (isParentLevel) {
            entry.setId(parentUrl);
        } else {
            entry.setId(parentUrl + "/" + activityVersion.getUriKey());
        }
        entry.setTitle(activityVersion.getTitle());
        entry.setSummary(activityVersion.getSummary());
        entry.setContent(activityVersion.getContent());
        entry.setUpdated(activityVersion.getUpdated());
        Set<String> authors = activityVersion.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }

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
        entry.addLink(parentUrl, Constants.REL_TYPE_LATEST_VERSION);


        SortedSet<ActivityVersion> versions = activityVersion.getParent().getVersions();
        ActivityVersion[] versionArray = new ActivityVersion[versions.size()];
        versionArray = activityVersion.getParent().getVersions().toArray(versionArray);
        ActivityVersion successorVersion = null;
        ActivityVersion predecessorVersion = null;
        for (int i = 0; i < versionArray.length; i++) {
            if (versionArray[i].equals(activityVersion)) {
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


        return entry;
    }

    public static Entry getEntryFromParty(PartyVersion partyVersion, boolean isParentLevel) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_PARTIES + "/" + partyVersion.getParent().getUriKey();
        if (isParentLevel) {
            entry.setId(parentUrl);
        } else {
            entry.setId(parentUrl + "/" + partyVersion.getUriKey());
        }
        entry.setTitle(partyVersion.getTitle());
        entry.setSummary(partyVersion.getSummary());
        entry.setContent(partyVersion.getContent());
        entry.setUpdated(partyVersion.getUpdated());
        Set<String> authors = partyVersion.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }

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

        entry.addLink(parentUrl, Constants.REL_TYPE_LATEST_VERSION);

        SortedSet<PartyVersion> versions = partyVersion.getParent().getVersions();
        PartyVersion[] versionArray = new PartyVersion[versions.size()];
        versionArray = partyVersion.getParent().getVersions().toArray(versionArray);
        PartyVersion successorVersion = null;
        PartyVersion predecessorVersion = null;
        for (int i = 0; i < versionArray.length; i++) {
            if (versionArray[i].equals(partyVersion)) {
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
        return entry;
    }

    public static Entry getEntryFromCollection(CollectionVersion collectionVersion, boolean isParentLevel) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collectionVersion.getParent().getUriKey();
        if (isParentLevel) {
            entry.setId(parentUrl);
        } else {
            entry.setId(parentUrl + "/" + collectionVersion.getUriKey());
        }
        entry.setTitle(collectionVersion.getTitle());
        entry.setSummary(collectionVersion.getSummary());
        entry.setContent(collectionVersion.getContent());
        entry.setUpdated(collectionVersion.getUpdated());
        Set<String> authors = collectionVersion.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }
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
        entry.addLink(parentUrl, Constants.REL_TYPE_LATEST_VERSION);

        SortedSet<CollectionVersion> versions = collectionVersion.getParent().getVersions();
        CollectionVersion[] versionArray = new CollectionVersion[versions.size()];
        versionArray = collectionVersion.getParent().getVersions().toArray(versionArray);
        CollectionVersion successorVersion = null;
        CollectionVersion predecessorVersion = null;
        for (int i = 0; i < versionArray.length; i++) {
            if (versionArray[i].equals(collectionVersion)) {
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

        return entry;
    }

    public static Entry getEntryFromService(ServiceVersion serviceVersion, boolean isParentLevel) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        String parentUrl = Constants.ID_PREFIX + Constants.PATH_FOR_SERVICES + "/" + serviceVersion.getParent().getUriKey();
        if (isParentLevel) {
            entry.setId(parentUrl);
        } else {
            entry.setId(parentUrl + "/" + serviceVersion.getUriKey());
        }
        entry.setTitle(serviceVersion.getTitle());
        entry.setSummary(serviceVersion.getSummary());
        entry.setContent(serviceVersion.getContent());
        entry.setUpdated(serviceVersion.getUpdated());
        Set<String> authors = serviceVersion.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }
        entry.addSimpleExtension(Constants.QNAME_LOCATION, serviceVersion.getLocation());

        Set<Collection> collectionSet = serviceVersion.getSupportedBy();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.QNAME_SUPPORTED_BY);
            collectorOfElement.setAttributeValue(Constants.ATTRIBUTE_NAME_URI, Constants.ID_PREFIX + Constants.PATH_FOR_COLLECTIONS + "/" + collection.getUriKey());
        }

        entry.addLink(parentUrl, Constants.REL_TYPE_LATEST_VERSION);

        SortedSet<ServiceVersion> versions = serviceVersion.getParent().getVersions();
        ServiceVersion[] versionArray = new ServiceVersion[versions.size()];
        versionArray = serviceVersion.getParent().getVersions().toArray(versionArray);
        ServiceVersion successorVersion = null;
        ServiceVersion predecessorVersion = null;
        for (int i = 0; i < versionArray.length; i++) {
            if (versionArray[i].equals(serviceVersion)) {
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

    public static boolean updatePartyFromEntry(PartyVersion partyVersion, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            partyVersion.setTitle(entry.getTitle());
            partyVersion.setSummary(entry.getSummary());
            partyVersion.setContent(entry.getContent());
            partyVersion.setUpdated(entry.getUpdated());
            partyVersion.setAuthors(getAuthors(entry.getAuthors()));
            return true;
        }
    }

    public static boolean updateServiceFromEntry(ServiceVersion serviceVersion, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            serviceVersion.setTitle(entry.getTitle());
            serviceVersion.setSummary(entry.getSummary());
            serviceVersion.setContent(entry.getContent());
            serviceVersion.setUpdated(entry.getUpdated());
            serviceVersion.setAuthors(getAuthors(entry.getAuthors()));
            List<Element> extensions = entry.getExtensions();
            for (Element extension : extensions) {
                if (extension.getQName().equals(Constants.QNAME_LOCATION)) {
                    serviceVersion.setLocation(extension.getText());
                }
            }
            return true;
        }
    }

    public static boolean isValidActivityFromEntry(ActivityVersion activityVersion, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            activityVersion.setTitle(entry.getTitle());
            activityVersion.setSummary(entry.getSummary());
            activityVersion.setContent(entry.getContent());
            activityVersion.setUpdated(entry.getUpdated());
            activityVersion.setAuthors(getAuthors(entry.getAuthors()));
            return true;
        }
    }

    public static boolean updateCollectionFromEntry(CollectionVersion collectionVersion, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            collectionVersion.setTitle(entry.getTitle());
            collectionVersion.setSummary(entry.getSummary());
            collectionVersion.setContent(entry.getContent());
            collectionVersion.setUpdated(entry.getUpdated());
            collectionVersion.setAuthors(getAuthors(entry.getAuthors()));
            List<Element> extensions = entry.getExtensions();
            for (Element extension : extensions) {
                if (extension.getQName().equals(Constants.QNAME_LOCATION)) {
                    collectionVersion.setLocation(extension.getText());
                }
            }
            return true;
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


}
