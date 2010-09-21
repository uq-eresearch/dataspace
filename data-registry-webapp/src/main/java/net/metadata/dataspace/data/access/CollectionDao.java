package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.model.Collection;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:11:41 AM
 */
public interface CollectionDao extends Dao<Collection> {

    /**
     * Retrieve a collection from the database by the provided id
     *
     * @param id
     * @return Collection
     */
    Collection getById(Long id);

    /**
     * Retrieve a collection from the dataase by the provided key uri
     *
     * @param keyURI
     * @return Collection
     */
    Collection getByKey(String keyURI);
}
