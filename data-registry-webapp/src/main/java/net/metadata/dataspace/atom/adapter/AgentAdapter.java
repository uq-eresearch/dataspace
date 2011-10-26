package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;

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
}
