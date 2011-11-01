package net.metadata.dataspace.atom.adapter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 4:59:19 PM
 */
@Transactional
public class AgentAdapter extends AbstractRecordAdapter<Agent,AgentVersion> {

	@Override
	protected Class<Agent> getRecordClass() {
		return Agent.class;
	}

    @Override
    protected String getLinkTerm() {
    	return Constants.TERM_AGENT_AS_PERSON;
    }

    @Override
    protected String getTitle() {
        return Constants.TITLE_FOR_AGENTS;
    }

	@Override
	protected String getBasePath() {
		return Constants.PATH_FOR_AGENTS;
	}

    @Override
    @Transactional(readOnly=true)
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = OperationHelper.getEntryID(request);
        try {
        	// Check it's an email address
        	Agent agent = getAgentWithEmail(new InternetAddress(uriKey));
        	if (agent != null) {
        		return getCanonicalRedirect(agent);
        	}
        } catch (AddressException address) {
        	// No action required - we'll try the default behaviour
        }
    	return super.getEntry(request);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseContext postEntry(RequestContext request) {
    	try {
    		enforceAuthentication(request);
			Entry entry = getEntryFromRequest(request);
			for (Link link : entry.getLinks(Constants.REL_MBOX)) {
				InternetAddress emailAddress =
						getAdapterInputHelper().getEmailFromHref(link.getHref());
				Agent existingAgent = getAgentWithEmail(emailAddress);
				if (existingAgent != null) {
					throw new ResponseContextException(
		            		"Agent already exists: "+existingAgent, 409);
				}
			}
    	} catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    	return super.postEntry(request);
    }

    protected Agent getAgentWithEmail(InternetAddress email) {
    	Agent agent = ((AgentDao)getDao()).getByEmail(email);
    	return agent != null && agent.isActive() ? agent : null;
    }

    protected ResponseContext getCanonicalRedirect(Agent agent) {
    	String location = getId(agent);
		ResponseContext response = new EmptyResponseContext(303,
				"Agent found with email address");
		response.setLocation(location);
		return response;
    }

}
