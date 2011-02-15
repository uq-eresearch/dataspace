package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.base.Agent;
import net.metadata.dataspace.data.model.version.AgentVersion;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:21:30 PM
 */
public interface AgentDao extends Dao<Agent>, RegistryDao<Agent> {

    AgentVersion getByVersion(String uriKey, String version);
}
