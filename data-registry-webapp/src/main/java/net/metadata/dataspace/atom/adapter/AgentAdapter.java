package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.record.Agent;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
@Transactional
public class AgentAdapter extends AbstractRecordAdapter<Agent> {

	@Override
	protected Class<Agent> getRecordClass() {
		return Agent.class;
	}

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        getHttpMethodHelper().addFeedDetails(feed, request, getRecordClass());
        Iterable<Agent> entries = getEntries(request);
        if (entries != null) {
            for (Agent entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                getFeedOutputHelper().setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                    Link typeLink = e.addLink(Constants.TERM_AGENT_AS_PERSON, Constants.REL_TYPE);
                    typeLink.setTitle("Agent");
                }
            }
        }
    }

    @Override
    public String[] getAccepts(RequestContext request) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
    }

    @Override
    public Agent postEntry(String title, IRI iri, String summary, Date updated, List<Person> authors, Content content,
                           RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    @Override
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        getDao().softDelete(key);
    }

    @Override
    public Object getContent(Agent agent, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(agent.getContent());
        return content;
    }

    @Override
    public Iterable<Agent> getEntries(RequestContext requestContext) throws ResponseContextException {
        return getRecords(requestContext);
    }

    @Override
    public Agent getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        Agent agent = getDao().getByKey(key);
        if (agent != null) {
            getDao().refresh(agent);
        }
        return agent;
    }

    @Override
    public String getId(Agent agent) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey();
    }

    @Override
    public String getName(Agent agent) throws ResponseContextException {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS + "/" + agent.getUriKey();
    }

    @Override
    public String getTitle(Agent agent) throws ResponseContextException {
        return agent.getTitle();
    }

    @Override
    public Date getUpdated(Agent agent) throws ResponseContextException {
        return agent.getUpdated();
    }

    @Override
    public void putEntry(Agent agent, String title, Date updated, List<Person> authors, String summary, Content content,
                         RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported");
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return Constants.UQ_REGISTRY_URI_PREFIX + Constants.PATH_FOR_AGENTS;
    }

    @Override
    public String getTitle(RequestContext requestContext) {
        return Constants.TITLE_FOR_AGENTS;
    }
}
