package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.model.Collection;
import net.metadata.dataspace.model.Party;
import net.metadata.dataspace.util.CollectionAdapterHelper;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.json.JSONWriter;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionCollectionAdapter extends AbstractEntityCollectionAdapter<Collection> {

    private Logger logger = Logger.getLogger(getClass());
    private CollectionDao collectionDao = DataRegistryApplication.getApplicationContext().getCollectionDao();
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getPartyDao();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix();

    @Override
    public Collection postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors,
                                Content content, RequestContext requestContext) throws ResponseContextException {
        Collection collection = new Collection();
        collection.setTitle(title);
        collection.setSummary(summary);
        collection.setUpdated(updated);
        collection.setAuthors(getAuthors(authors));
        collectionDao.save(collection);

        return collection;
    }


    @Override
    public Collection postMedia(MimeType mimeType, String slug, InputStream inputStream, RequestContext request) throws ResponseContextException {
        logger.info("Persisting Collection as Media Entry");

        if (mimeType.getBaseType().equals("application/json")) {
            String jsonString = getJsonString(inputStream);
            Collection collection = new Collection();
            assembleCollectionFromJson(collection, jsonString);
            collectionDao.save(collection);
            return collection;
        }
        return null;
    }

    @Override
    public String getMediaName(Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public String getContentType(Collection collection) {
        return Constants.JSON_MIMETYPE;
    }


    @Override
    public void putEntry(Collection collection, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext requestContext) throws ResponseContextException {

        collectionDao.update(collection);
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        logger.info("Updating Collection as Media Entry");
        if (request.getContentType().getBaseType().equals(Constants.JSON_MIMETYPE)) {
            InputStream inputStream = null;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                logger.fatal("Cannot create inputstream from request.", e);
            }
            String collectionAsJsonString = getJsonString(inputStream);
            String uriKey = CollectionAdapterHelper.getEntryID(request);
            Collection collection = collectionDao.getByKey(uriKey);
            assembleCollectionFromJson(collection, collectionAsJsonString);
            collectionDao.update(collection);
        }
        return getEntry(request);
    }


    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        collectionDao.delete(collectionDao.getByKey(key));
    }

    @Override
    public Collection getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getByKey(key);
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {

        String uriKey = CollectionAdapterHelper.getEntryID(request);
        Collection collection = collectionDao.getByKey(uriKey);
        Abdera abdera = new Abdera();
        Entry entry = abdera.newEntry();
        entry.setId(collection.getUriKey());
        entry.setTitle(collection.getTitle());
        entry.setSummary(collection.getSummary());
        entry.setUpdated(collection.getUpdated());

        ResponseContext responseContext = ProviderHelper.returnBase(entry, 200, collection.getUpdated())
                .setEntityTag(ProviderHelper.calculateEntityTag(entry));
        if (request.getAccept().equals(Constants.JSON_MIMETYPE)) {
            responseContext.setContentType(Constants.JSON_MIMETYPE);
            responseContext.setWriter(new JSONWriter());
        } else {
            responseContext.setContentType(Constants.ATOM_MIMETYPE);
            responseContext.setWriter(new PrettyWriter());
        }
        return responseContext;
    }

    public List<Person> getAuthors(Collection collection, RequestContext request) throws ResponseContextException {
        Set<String> authors = collection.getAuthors();
        List<Person> personList = new ArrayList<Person>();
        for (String author : authors) {
            Person person = request.getAbdera().getFactory().newAuthor();
            person.setName(author);
            personList.add(person);
        }
        return personList;
    }

    @Override
    public Object getContent(Collection collection, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(collection.getSummary());
        return content;
    }

    @Override
    public Iterable<Collection> getEntries(RequestContext requestContext) throws ResponseContextException {
        return collectionDao.getAll();
    }

    @Override
    public String getId(Collection collection) throws ResponseContextException {
        return collection.getUriKey();
    }

    @Override
    public String getName(Collection collection) throws ResponseContextException {
        return collection.getUriKey();
    }

    @Override
    public String getTitle(Collection collection) throws ResponseContextException {
        return collection.getTitle();
    }

    @Override
    public Date getUpdated(Collection collection) throws ResponseContextException {
        return collection.getUpdated();
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return ID_PREFIX + "collections";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Collections";
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.ATOM_MIMETYPE + ";type=entry", Constants.JSON_MIMETYPE};
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

    private void assembleCollectionFromJson(Collection collection, String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            collection.setTitle(jsonObj.getString("title"));
            collection.setSummary(jsonObj.getString("summary"));
            collection.setUpdated(new Date());
            collection.setLocation(jsonObj.getString("location"));

            JSONArray collectors = jsonObj.getJSONArray("collector");
            Set<Party> parties = new HashSet<Party>();
            for (int i = 0; i < collectors.length(); i++) {
                parties.add(partyDao.getByKey(collectors.getString(i)));
            }
            collection.setCollector(parties);

            JSONArray authors = jsonObj.getJSONArray("authors");
            Set<String> persons = new HashSet<String>();
            for (int i = 0; i < authors.length(); i++) {
                persons.add(authors.getString(i));
            }
            collection.setAuthors(persons);
        } catch (JSONException ex) {
            logger.fatal("Could not assemble collection from JSON object", ex);
        }
    }
}