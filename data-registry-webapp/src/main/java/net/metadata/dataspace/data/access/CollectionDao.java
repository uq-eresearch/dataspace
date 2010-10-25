package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.Collection;

import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:11:41 AM
 */
public interface CollectionDao extends Dao<Collection> {

    /**
     * Retrieve a collection from the database by the provided id
     *
     * @param id a collection id
     * @return Collection
     */
    Collection getById(Long id);

    /**
     * Retrieve a collection from the database by the uri key
     *
     * @param uriKey a 31 base uri key
     * @return Collection
     */
    Collection getByKey(String uriKey);

    /**
     * Soft delete a collection by setting isActive flag to false
     *
     * @param uriKey
     * @return rows affected
     */
    int softDelete(String uriKey);

    /**
     * Returns all active collections
     *
     * @return List of collections
     */
    List<Collection> getAllActive();

    /**
     * Returns all inactive collections
     *
     * @return List of inactive collections
     */
    List<Collection> getAllInActive();

    /**
     * Get latest updated collection
     *
     * @return
     */
    Collection getLatestCollection();
}
