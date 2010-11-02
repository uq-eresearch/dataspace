package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.CollectionVersion;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:11:41 AM
 */
public interface CollectionDao extends Dao<Collection>, RegistryDao<Collection> {

    CollectionVersion getByVersion(String uriKey, String version);

}
