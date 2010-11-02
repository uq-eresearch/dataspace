package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.PartyVersion;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:21:30 PM
 */
public interface PartyDao extends Dao<Party>, RegistryDao<Party> {

    PartyVersion getByVersion(String uriKey, String version);
}
