package net.metadata.dataspace.atom.adapter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
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

    public Agent getAgentWithEmail(InternetAddress email) {
    	return ((AgentDao)getDao()).getByEmail(email);
    }

    public ResponseContext getCanonicalRedirect(Agent agent) {
    	String location = getId(agent);
		ResponseContext response = new EmptyResponseContext(303,
				"Agent found with email address");
		response.setLocation(location);
		return response;
    }

}
