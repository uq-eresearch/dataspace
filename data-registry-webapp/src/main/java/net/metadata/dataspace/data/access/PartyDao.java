package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.Party;

import java.util.List;

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

    /**
     * Retrieves a party from database by the uri key
     *
     * @param uriKey a 31 base uri key
     * @return a Party object
     */
    Party getByKey(String uriKey);

    /**
     * Soft delete a party by setting isActive flag to false
     *
     * @param uriKey
     * @return rows affected
     */
    int softDelete(String uriKey);

    /**
     * Returns all active parties
     *
     * @return List of parties
     */
    List<Party> getAllActive();

    /**
     * Returns all inactive parties
     *
     * @return List of inactive parties
     */
    List<Party> getAllInActive();

    /**
     * Gets the latest updated party
     *
     * @return
     */
    Party getLatestParty();
}
