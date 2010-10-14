package net.metadata.dataspace.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.model.Party;
import net.metadata.dataspace.model.Subject;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * User: alabri
 * Date: 06/10/2010
 * Time: 11:30:29 AM
 */
public class CollectionAdapterHelper {

    private static Logger logger = Logger.getLogger(CollectionAdapterHelper.class);
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();
    private static final QName SUBJECT_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "subject", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    private static final QName COLLECTOR_OF_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "collectorOf", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    private static final QName COLLECTOR_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "collector", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);
    private static final QName LOCATION_QNAME = new QName(Constants.UQ_DATA_COLLECTIONS_REGISTRY_NS, "location", Constants.UQ_DATA_COLLECTIONS_REGISTRY_PFX);

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
        if (fullUrl.contains("?")) {
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
        entry.setId(ID_PREFIX + "parties/" + party.getUriKey());
        entry.setTitle(party.getTitle());
        entry.setSummary(party.getSummary());
        entry.setUpdated(party.getUpdated());
        Set<Subject> subjectSet = party.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(SUBJECT_QNAME);
            subjectElement.setAttributeValue("vocabulary", sub.getVocabulary());
            subjectElement.setAttributeValue("value", sub.getValue());
        }

        Set<Collection> collectionSet = party.getCollectorOf();
        for (Collection collection : collectionSet) {
            Element collectorOfElement = entry.addExtension(COLLECTOR_OF_QNAME);
            collectorOfElement.setAttributeValue("uri", ID_PREFIX + "collections/" + collection.getUriKey());
        }
        return entry;
    }

    public static Entry getEntryFromCollection(Collection collection) {
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(ID_PREFIX + "collections/" + collection.getUriKey());
        entry.setTitle(collection.getTitle());
        entry.setSummary(collection.getSummary());
        entry.setUpdated(collection.getUpdated());
        entry.addSimpleExtension(LOCATION_QNAME, collection.getLocation());
        Set<Subject> subjectSet = collection.getSubjects();
        for (Subject sub : subjectSet) {
            Element subjectElement = entry.addExtension(SUBJECT_QNAME);
            subjectElement.setAttributeValue("vocabulary", sub.getVocabulary());
            subjectElement.setAttributeValue("value", sub.getValue());
        }

        Set<Party> partySet = collection.getCollector();
        for (Party sub : partySet) {
            Element partyElement = entry.addExtension(COLLECTOR_QNAME);
            partyElement.setAttributeValue("uri", ID_PREFIX + "parties/" + sub.getUriKey());
        }

        entry.addLink(ID_PREFIX + "collections/" + collection.getUriKey(), "alternate");
        return entry;
    }
}
