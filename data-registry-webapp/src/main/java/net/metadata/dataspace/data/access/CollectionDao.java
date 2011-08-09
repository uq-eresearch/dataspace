package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.Collection;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:11:41 AM
 */
public interface CollectionDao extends RegistryDao<Collection> {

    Version getByVersion(String uriKey, String version);

    Collection getByOriginalId(String originalId);
}
