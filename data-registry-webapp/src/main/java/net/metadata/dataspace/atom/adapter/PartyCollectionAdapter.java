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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyCollectionAdapter extends AbstractEntityCollectionAdapter<Party> {
    private Logger logger = Logger.getLogger(PartyCollectionAdapter.class.getName());
    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getPartyDao();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix() + "party/";

    @Override
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        Party party = new Party();
        try {
            party.setTitle(title);
            party.setSummary(summary);
            party.setUpdated(updated);
            party.setAuthors(getAuthors(authors));
            partyDao.save(party);
        } catch (Exception ex) {
            System.out.println(party.getId() + " " + party.getKey() + " " + title + " " + summary + " " + updated.toString() + " " + getAuthors(authors).size());
            ex.printStackTrace();
        }
        return party;
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        partyDao.update(party);
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

    public List<Person> getAuthors(Party entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("UQ");
        return Arrays.asList(author);
    }

    @Override
    public Object getContent(Party party, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(party.getSummary());
        return content;
    }

    @Override
    public String getId(Party party) throws ResponseContextException {
        return ID_PREFIX + party.getKey();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        return party.getTitle();
    }

    @Override
    public String getTitle(Party party) throws ResponseContextException {
        //TODO Review, this corresponds to party name
        return "Party: " + party.getTitle();
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
        return ID_PREFIX + "party/parties";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Parties";
    }

    private List<String> getAuthors(List<Person> persons) {
        List<String> authors = new ArrayList<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }
}
