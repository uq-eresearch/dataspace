package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.record.Agent;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
public class AgentAdapter extends AbstractEntityCollectionAdapter<Agent> {

    private Logger logger = Logger.getLogger(getClass());
    private AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();

    @Override
    public ResponseContext postEntry(RequestContext request) {
        try {
            return HttpMethodHelper.postEntry(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext postMedia(RequestContext request) {
        try {
            return HttpMethodHelper.postMedia(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putEntry(RequestContext request) {
        try {
            return HttpMethodHelper.putEntry(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext putMedia(RequestContext request) {
        try {
            return HttpMethodHelper.putMedia(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            return HttpMethodHelper.deleteEntry(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        try {
            return HttpMethodHelper.getEntry(request, Agent.class);
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
        try {
            String representationMimeType = FeedOutputHelper.getRepresentationMimeType(request);
            String accept = request.getAccept();
            if (representationMimeType == null) {
                representationMimeType = accept;
            }
            if (representationMimeType != null &&
                    (representationMimeType.equals(Constants.MIME_TYPE_ATOM_FEED) ||
                            representationMimeType.equals(Constants.MIME_TYPE_ATOM))) {
                return super.getFeed(request);
            } else {
                Feed feed = createFeedBase(request);
                addFeedDetails(feed, request);
                ResponseContext responseContext = buildGetFeedResponse(feed);
                return HttpMethodHelper.getFeed(request, responseContext, Agent.class);
            }
        } catch (ResponseContextException e) {
            return OperationHelper.createResponse(e.getStatusCode(), e.getMessage());
        }
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
        HttpMethodHelper.addFeedDetails(feed, request, Agent.class);
        Iterable<Agent> entries = getEntries(request);
        if (entries != null) {
            for (Agent entryObj : entries) {
                Entry e = feed.addEntry();
                IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
                addEntryDetails(request, e, feedIri, entryObj);
                FeedOutputHelper.setPublished(entryObj, e);
                if (isMediaEntry(entryObj)) {
                    addMediaContent(feedIri, e, entryObj, request);
                } else {
                    addContent(e, entryObj, request);
                }
            }
        }
    }

    public List<Person> getAuthors(Agent agent, RequestContext request) throws ResponseContextException {
        return HttpMethodHelper.getAuthors(agent, request);
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
        agentDao.softDelete(key);
    }

    @Override
    public Object getContent(Agent agent, RequestContext requestContext) throws ResponseContextException {
        Content content = requestContext.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(agent.getContent());
        return content;
    }

    @Override
    public Iterable<Agent> getEntries(RequestContext requestContext) throws ResponseContextException {
        return HttpMethodHelper.getRecords(requestContext, Agent.class);
    }

    @Override
    public Agent getEntry(String key, RequestContext requestContext) throws ResponseContextException {
        Agent agent = agentDao.getByKey(key);
        if (agent != null) {
            agentDao.refresh(agent);
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
