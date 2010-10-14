package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.model.Party;
import net.metadata.dataspace.model.Subject;
import net.metadata.dataspace.util.CollectionAdapterHelper;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.json.JSONWriter;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyCollectionAdapter extends AbstractEntityCollectionAdapter<Party> {

    private Logger logger = Logger.getLogger(getClass());
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getPartyDao();
    private SubjectDao subjectDao = DataRegistryApplication.getApplicationContext().getSubjectDao();
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getCollectionDao();

    private final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();

    @Override
    public ResponseContext postEntry(RequestContext request) {

        try {
            Document<Entry> feed_doc = request.getDocument();
            Entry root = feed_doc.getRoot();
            System.out.println(root);
        } catch (IOException e) {
            logger.fatal("Invalid feed document", e);
        }

        return super.postEntry(request);
    }

    @Override
    public Party postMedia(MimeType mimeType, String slug, InputStream inputStream, RequestContext request) throws ResponseContextException {
        logger.info("Persisting Party as Media Entry");

        if (mimeType.getBaseType().equals(Constants.JSON_MIMETYPE)) {
            Party party = new Party();
            String partyAsJsonString = CollectionAdapterHelper.getJsonString(inputStream);
            assembleParty(party, partyAsJsonString);
            return party;
        }
        return null;
    }

    @Override
    public String getMediaName(Party party) throws ResponseContextException {
        return party.getTitle();
    }

    @Override
    public String getContentType(Party party) {
        return Constants.JSON_MIMETYPE;
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        logger.info("Updating Party as Media Entry");

        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
            }
            String partyAsJsonString = CollectionAdapterHelper.getJsonString(inputStream);
            String uriKey = CollectionAdapterHelper.getEntryID(request);
            Party party = partyDao.getByKey(uriKey);
            assembleParty(party, partyAsJsonString);
            partyDao.update(party);
        }
        return getEntry(request);
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {

        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            Entry entry = CollectionAdapterHelper.getEntryFromParty(party);
            if (party.isActive()) {
                try {
                    deleteEntry(uriKey, request);
                    return ProviderHelper.returnBase(entry, 200, party.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
                } catch (ResponseContextException e) {
                    logger.fatal("Could not delete party entry");
                    return ProviderHelper.servererror(request, e);
                }
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }


    public ResponseContext getEntry(RequestContext request) {
        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Party party = partyDao.getByKey(uriKey);
        if (party == null) {
            return ProviderHelper.notfound(request);
        } else {
            if (party.isActive()) {
                Entry entry = CollectionAdapterHelper.getEntryFromParty(party);
                ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, party.getUpdated()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
                if (request.getAccept().equals(Constants.JSON_MIMETYPE)) {
                    responseContext.setContentType(Constants.JSON_MIMETYPE);
                    responseContext.setWriter(new JSONWriter());
                } else if (request.getAccept().equals(Constants.ATOM_MIMETYPE)) {
                    responseContext.setContentType(Constants.ATOM_MIMETYPE);
                    responseContext.setWriter(new PrettyWriter());
                } else {
                    return ProviderHelper.createErrorResponse(new Abdera(), 406, "The requested entry cannot be supplied in " + request.getAccept() + " mime type.");
                }
                return responseContext;
            } else {
                return ProviderHelper.createErrorResponse(new Abdera(), 410, "The requested entry is no longer available.");
            }
        }
    }

    @Override
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        Party party = new Party();
        logger.info("Persisting Party: " + title);
        try {
            party.setTitle(title);
            party.setSummary(summary);
            party.setUpdated(updated);
            party.setAuthors(getAuthors(authors));
            partyDao.save(party);
        } catch (Exception ex) {
            logger.fatal("Error Persisting Party: " + title);
        }
        return party;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        partyDao.softDelete(key);
    }

    @Override
    public Party getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        return partyDao.getByKey(key);
    }

    @Override
    public Iterable<Party> getEntries(RequestContext requestContext) throws ResponseContextException {
        return partyDao.getAllActive();
    }

    public List<Person> getAuthors(Party party, RequestContext request) throws ResponseContextException {
        Set<String> authors = party.getAuthors();
        List<Person> personList = new ArrayList<Person>();
        for (String author : authors) {
            Person person = request.getAbdera().getFactory().newAuthor();
            person.setName(author);
            personList.add(person);
        }
        return personList;
    }

    @Override
    public Object getContent(Party party, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(party.getSummary());
        return content;
    }

    @Override
    public String getId(Party party) throws ResponseContextException {
        return ID_PREFIX + "parties/" + party.getUriKey();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        //TODO this sets the link element which contains the edit link
        return ID_PREFIX + "parties/" + party.getUriKey();
    }

    @Override
    public String getTitle(Party party) throws ResponseContextException {
        return party.getTitle();
    }

    @Override
    public Date getUpdated(Party party) throws ResponseContextException {
        return party.getUpdated();
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return ID_PREFIX + "parties";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Parties";
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_MIMETYPE + ";type=entry", Constants.JSON_MIMETYPE};
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported");
    }

    private Set<String> getAuthors(List<Person> persons) {
        Set<String> authors = new HashSet<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }

    private void assembleParty(Party party, String jsonString) {

        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            party.setTitle(jsonObj.getString("title"));
            party.setSummary(jsonObj.getString("summary"));
            party.setUpdated(new Date());

            JSONArray authors = jsonObj.getJSONArray("authors");
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            party.setAuthors(persons);

            if (party.getId() == null) {
                partyDao.save(party);
            }

            JSONArray subjectArray = jsonObj.getJSONArray("subject");
            for (int i = 0; i < subjectArray.length(); i++) {
                Subject subject = new Subject(subjectArray.getJSONObject(i).getString("vocabulary"), subjectArray.getJSONObject(i).getString("value"));
                party.getSubjects().add(subject);
                subjectDao.save(subject);
            }
            partyDao.update(party);
            JSONArray collectionArray = jsonObj.getJSONArray("collectorof");
            for (int i = 0; i < collectionArray.length(); i++) {
                Collection collection = collectionDao.getByKey(collectionArray.getString(i));
                if (collection != null) {
                    collection.getCollector().add(party);
                    party.getCollectorOf().add(collection);
                }
            }
            partyDao.update(party);
        } catch (JSONException ex) {
            logger.fatal("Could not assemble party from JSON object", ex);
        }
    }
}
