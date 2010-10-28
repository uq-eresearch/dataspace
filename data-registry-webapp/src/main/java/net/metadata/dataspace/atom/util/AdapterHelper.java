package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
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

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
public class AdapterHelper {

    private static Logger logger = Logger.getLogger(AdapterHelper.class);
    private static final EntityCreator entityCreator = DataRegistryApplication.getApplicationContext().getEntityCreator();

    public static String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY) {
            return null;
        }

        String fullUrl = request.getUri().toString();
        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }

        String[] segments = fullUrl.split("/");
        return UrlEncoding.decode(segments[segments.length - 1]);
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
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.fatal("Could not close inputstream", ex);
                }
            }
        }
        String jsonString = sb.toString();
        return jsonString;
    }

    public static Entry getEntryFromParty(Party party) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(Constants.ID_PREFIX + Constants.PARTIES_PATH + "/" + party.getUriKey());
        entry.setTitle(party.getTitle());
        entry.setSummary(party.getSummary());
        entry.setContent(party.getContent());
        entry.setUpdated(party.getUpdated());
        Set<String> authors = party.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }

        Set<Subject> subjectSet = party.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(Constants.SUBJECT_QNAME);
            subjectElement.setAttributeValue("vocabulary", sub.getVocabulary());
            subjectElement.setAttributeValue("value", sub.getValue());
        }

        Set<Collection> collectionSet = party.getCollectorOf();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.COLLECTOR_OF_QNAME);
            collectorOfElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey());
        }
        entry.addLink(Constants.ID_PREFIX + Constants.PARTIES_PATH + "/" + party.getUriKey(), "alternate");
        return entry;
    }

    public static Entry getEntryFromCollection(Collection collection) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey());
        entry.setTitle(collection.getTitle());
        entry.setSummary(collection.getSummary());
        entry.setContent(collection.getContent());
        entry.setUpdated(collection.getUpdated());
        Set<String> authors = collection.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }
        entry.addSimpleExtension(Constants.LOCATION_QNAME, collection.getLocation());
        Set<Subject> subjectSet = collection.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(Constants.SUBJECT_QNAME);
            subjectElement.setAttributeValue("vocabulary", sub.getVocabulary());
            subjectElement.setAttributeValue("value", sub.getValue());
        }

        Set<Party> parties = collection.getCollector();
        for (Party party : parties) {
            Element partyElement = entry.addExtension(Constants.COLLECTOR_QNAME);
            partyElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.PARTIES_PATH + "/" + party.getUriKey());
        }

        Set<Service> services = collection.getSupports();
        for (Service service : services) {
            Element serviceElement = entry.addExtension(Constants.SUPPORTS_QNAME);
            serviceElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.SERVICES_PATH + "/" + service.getUriKey());
        }

        Set<Activity> activities = collection.getOutputOf();
        for (Activity activity : activities) {
            Element serviceElement = entry.addExtension(Constants.IS_OUTPUT_OF_QNAME);
            serviceElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + activity.getUriKey());
        }

        entry.addLink(Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey(), "alternate");
        return entry;
    }

    public static Entry getEntryFromActivity(Activity activity) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(Constants.ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + activity.getUriKey());
        entry.setTitle(activity.getTitle());
        entry.setSummary(activity.getSummary());
        entry.setContent(activity.getContent());
        entry.setUpdated(activity.getUpdated());
        Set<String> authors = activity.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }

        Set<Party> partySet = activity.getHasParticipant();
        for (Party sub : partySet) {
            Element partyElement = entry.addExtension(Constants.HAS_PARTICIPANT_QNAME);
            partyElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.PARTIES_PATH + "/" + sub.getUriKey());
        }

        Set<Collection> collectionSet = activity.getHasOutput();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.HAS_OUTPUT_QNAME);
            collectorOfElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey());
        }
        entry.addLink(Constants.ID_PREFIX + Constants.ACTIVITIES_PATH + "/" + activity.getUriKey(), "alternate");
        return entry;
    }

    public static Entry getEntryFromService(Service service) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(Constants.ID_PREFIX + Constants.SERVICES_PATH + "/" + service.getUriKey());
        entry.setTitle(service.getTitle());
        entry.setSummary(service.getSummary());
        entry.setContent(service.getContent());
        entry.setUpdated(service.getUpdated());
        Set<String> authors = service.getAuthors();
        for (String author : authors) {
            entry.addAuthor(author);
        }
        entry.addSimpleExtension(Constants.LOCATION_QNAME, service.getLocation());

        Set<Collection> collectionSet = service.getSupportedBy();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(Constants.SUPPORTED_BY_QNAME);
            collectorOfElement.setAttributeValue("uri", Constants.ID_PREFIX + Constants.COLLECTIONS_PATH + "/" + collection.getUriKey());
        }

        entry.addLink(Constants.ID_PREFIX + Constants.SERVICES_PATH + "/" + service.getUriKey(), "alternate");
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
        selfLink.setRel("self");
    }

    private static void prepareAlternateLink(Entry entry, String href, String mimeType) {
        Link alternateLink = entry.getAlternateLink();
        if (alternateLink == null) {
            alternateLink = entry.addLink(entry.getId().toString());
        }
        alternateLink.setHref(href);
        alternateLink.setMimeType(mimeType);
        alternateLink.setRel("alternate");
    }

    public static boolean updatePartyFromEntry(Party party, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            party.setTitle(entry.getTitle());
            party.setSummary(entry.getSummary());
            party.setContent(entry.getContent());
            party.setUpdated(entry.getUpdated());
            party.setAuthors(getAuthors(entry.getAuthors()));
            return true;
        }
    }

    public static boolean updateServiceFromEntry(Service service, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            service.setTitle(entry.getTitle());
            service.setSummary(entry.getSummary());
            service.setContent(entry.getContent());
            service.setUpdated(entry.getUpdated());
            service.setAuthors(getAuthors(entry.getAuthors()));
            List<Element> extensions = entry.getExtensions();
            for (Element extension : extensions) {
                if (extension.getQName().equals(Constants.LOCATION_QNAME)) {
                    service.setLocation(extension.getText());
                }
            }
            return true;
        }
    }

    public static boolean updateActivityFromEntry(Activity activity, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            activity.setTitle(entry.getTitle());
            activity.setSummary(entry.getSummary());
            activity.setContent(entry.getContent());
            activity.setUpdated(entry.getUpdated());
            activity.setAuthors(getAuthors(entry.getAuthors()));
            return true;
        }
    }

    public static boolean updateCollectionFromEntry(Collection collection, Entry entry) {
        if (entry == null || !ProviderHelper.isValidEntry(entry)) {
            return false;
        } else {
            collection.setTitle(entry.getTitle());
            collection.setSummary(entry.getSummary());
            collection.setContent(entry.getContent());
            collection.setUpdated(entry.getUpdated());
            collection.setAuthors(getAuthors(entry.getAuthors()));
            List<Element> extensions = entry.getExtensions();
            for (Element extension : extensions) {
                if (extension.getQName().equals(Constants.LOCATION_QNAME)) {
                    collection.setLocation(extension.getText());
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
            if (extension.getQName().equals(Constants.SUBJECT_QNAME)) {
                String vocabulary = extension.getAttributeValue("vocabulary");
                String value = extension.getAttributeValue("value");
                if (vocabulary != null && value != null) {
                    Subject subject = entityCreator.getNextSubject();
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
                String id = getEntityID(extension.getAttributeValue("uri"));
                if (id != null) {
                    uriKeys.add(id);
                }
            }
        }
        return uriKeys;
    }

    public static String getEntityID(String fullUrl) {

        if (fullUrl.contains("?")) {
            fullUrl = fullUrl.split("\\?")[0];
        }

        String[] segments = fullUrl.split("/");
        return UrlEncoding.decode(segments[segments.length - 1]);
    }

}
