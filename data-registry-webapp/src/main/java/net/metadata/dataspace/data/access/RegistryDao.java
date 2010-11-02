package net.metadata.dataspace.data.access;

import java.util.List;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:48:15 AM
 */
public interface RegistryDao<T> {

    /**
     * Retrieve an entity T from the database by the provided id
     *
     * @param id of an entity
     * @return entity T
     */
    T getById(Long id);

    /**
     * Retrieve an entity from the database by the uri key
     *
     * @param uriKey a 31 base uri key
     * @return entity T
     */
    T getByKey(String uriKey);

    /**
     * Soft delete an entity by setting isActive flag to false
     *
     * @param uriKey a 31 base uri key
     * @return rows affected
     */
    int softDelete(String uriKey);

    /**
     * Returns all active entities
     *
     * @return List of entities
     */
    List<T> getAllActive();

    /**
     * Returns all inactive entities
     *
     * @return List of inactive entities
     */
    List<T> getAllInactive();

    /**
     * Get the most recent updated entity
     *
     * @return entity
     */
    T getMostRecentUpdated();

    /**
     * Get the most recent inserted entity
     *
     * @return entity
     */
    T getMostRecentInserted();
}
