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

import java.util.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class PartyCollectionAdapter extends AbstractEntityCollectionAdapter<Party> {

    private PartyDao partyDao = DataRegistryApplication.getApplicationContext().getPartyDao();
    private static final String ID_PREFIX = DataRegistryApplication.getApplicationContext().getUriPrefix() + "party/";

    @Override
    public Party postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        Party party = new Party();
        party.setTitle(title);
        party.setCollectorOfURI(UUID.randomUUID().toString());
        party.setSummary(summary);
        party.setAuthors(getAuthors(authors));
        party.setUpdated(updated);
        partyDao.save(party);
        return party;
    }

    @Override
    public void putEntry(Party party, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        partyDao.update(party);
    }

    @Override
    public void deleteEntry(String id, RequestContext requestContext) throws ResponseContextException {
        partyDao.delete(partyDao.getById(id));
    }

    @Override
    public Party getEntry(String id, RequestContext requestContext) throws ResponseContextException {
        return partyDao.getById(id);
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
        return ID_PREFIX + party.getId();
    }

    @Override
    public String getName(Party party) throws ResponseContextException {
        return party.getTitle();
    }

    @Override
    public String getTitle(Party party) throws ResponseContextException {
        return "Party: " + party.getId();
    }

    @Override
    public Date getUpdated(Party party) throws ResponseContextException {
        return new Date();
    }


    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return DataRegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return DataRegistryApplication.getApplicationContext().getUriPrefix() + "party/feed";
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return "Party Collection";
    }

    private List<String> getAuthors(List<Person> persons) {
        List<String> authors = new ArrayList<String>();
        for (Person person : persons) {
            authors.add(person.getName());
        }
        return authors;
    }
}
