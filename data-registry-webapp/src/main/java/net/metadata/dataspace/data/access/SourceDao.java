package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.context.Source;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:46:12 PM
 */
public interface SourceDao extends RegistryDao<Source> {

    Source getBySourceURI(String sourceUri);

}
