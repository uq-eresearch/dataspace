package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.model.Party;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:21:30 PM
 */
public interface PartyDao extends Dao<Party> {
    /**
     * Retrieve a party from database by the provided id
     *
     * @param id
     * @return Party
     */
    Party getById(Long id);

    Party getByKey(String uriKey);
}
