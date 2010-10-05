package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.model.Party;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.MimeType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyCollectionAdapter extends AbstractEntityCollectionAdapter<Party> {
//    private Logger logger = Logger.getLogger(PartyCollectionAdapter.class.getName());

    private Logger logger = Logger.getLogger(getClass());
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getPartyDao();
//    private AtomicSequencer atomicSequencer = DataRegistryApplication.getApplicationContext().getAtomicSequencer();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();

    @Override
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        Party party = new Party();
        logger.info("Persisting Party: " + title);
        try {
//            party.setUriKey(atomicSequencer.nextBase31());
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
    public Party postMedia(MimeType mimeType, String slug, InputStream inputStream, RequestContext request) throws ResponseContextException {
        logger.info("Persisting Party as Media Entry");

        if (mimeType.getBaseType().equals("application/json")) {

            String jsonString = getJsonString(inputStream);

            Party party = new Party();
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
//                String uriKey = atomicSequencer.nextBase31();
//                party.setUriKey(uriKey);
                party.setTitle(jsonObj.getString("title"));
                party.setSummary(jsonObj.getString("summary"));
                party.setUpdated(new Date());
                JSONArray authors = jsonObj.getJSONArray("authors");
                Set<String> persons = new HashSet<String>();
                for (int i = 0; i < authors.length(); i++) {
                    persons.add(authors.getString(i));
                }
                party.setAuthors(persons);
            } catch (JSONException ex) {
                logger.fatal("Could not assemble party from JSON object", ex);
            }
            partyDao.save(party);
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
        return "application/json";
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        party.setTitle(title);
        party.setSummary(summary);
        party.setUpdated(updated);
        party.setAuthors(getAuthors(authors));
        partyDao.update(party);
    }

    @Override
    public void putMedia(Party entryObj, MimeType contentType, String slug, InputStream inputStream, RequestContext request) throws ResponseContextException {
        super.putMedia(entryObj, contentType, slug, inputStream, request);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        partyDao.delete(partyDao.getByKey(key));
    }

    @Override
    public Party getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        return partyDao.getByKey(key);
    }

    @Override
    public Iterable<Party> getEntries(RequestContext requestContext) throws ResponseContextException {
        return partyDao.getAll();
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
        return party.getUriKey();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        //TODO this sets the link element which contains the edit link
        return party.getUriKey();
    }

    @Override
    public String getTitle(Party party) throws ResponseContextException {
        //TODO Review, this corresponds to party name
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

    private Set<String> getAuthors(List<Person> persons) {
        Set<String> authors = new HashSet<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }

    private String getJsonString(InputStream inputStream) {
        //Parse the input stream to string
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

}
