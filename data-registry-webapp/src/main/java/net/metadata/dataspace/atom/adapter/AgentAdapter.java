package net.metadata.dataspace.atom.adapter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.model.context.Mbox;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
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
    public ResponseContext getEntry(RequestContext request) {
        String uriKey = OperationHelper.getEntryID(request);
        if (uriKey.contains("@")) {
            try {
	        	// Check it's an email address
	        	Agent agent = getAgentWithEmail(new InternetAddress(uriKey));
	        	if (agent == null) {
	        		return new EmptyResponseContext(404,
		    				"Unknown email address");
	        	}
	        	return getCanonicalRedirect(agent);
	        } catch (AddressException address) {
	        	return new EmptyResponseContext(400,
	    				"Invalid email address specified");
	        }
        }
    	return super.getEntry(request);
    }

    /**
     * Existing agents are detected based on email address as well as URI key.
     */
    @Override
    public Agent getExistingRecord(RequestContext request)
    		throws ResponseContextException
    {
    	// Try super method first
    	Agent existingAgent = super.getExistingRecord(request);
    	if (existingAgent != null) {
    		return existingAgent;
    	}
    	// If not, it's time for some Agent-specific searching
    	Entry entry = getEntryFromRequest(request);
		for (Link link : entry.getLinks(Constants.REL_MBOX)) {
			InternetAddress emailAddress =
					getAdapterInputHelper().getEmailFromHref(link.getHref());
			existingAgent = getAgentWithEmail(emailAddress);
			if (existingAgent != null) {
				return existingAgent;
			}
		}
		return null;
    }

    protected Agent getAgentWithEmail(InternetAddress email) {
    	Mbox mbox = getDaoManager().getMboxDao().getByEmail(email);
    	return mbox != null ? mbox.getOwner() : null;
    }

    protected ResponseContext getCanonicalRedirect(Agent agent) {
    	String location = getId(agent);
		ResponseContext response = new EmptyResponseContext(303,
				"Agent found with email address");
		response.setLocation(location);
		return response;
    }

	@Override
	protected Entry getEntryFromEntity(AgentVersion version,
			boolean isParentLevel) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        }
        return getAdapterOutputHelper().getEntryFromAgent(version, isParentLevel);
    }

}
