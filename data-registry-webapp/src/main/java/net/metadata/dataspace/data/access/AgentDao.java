package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:21:30 PM
 */
public interface AgentDao extends RegistryDao<Agent> {

    AgentVersion getByVersion(String uriKey, String version);

    Agent getByEmail(String email);
}
