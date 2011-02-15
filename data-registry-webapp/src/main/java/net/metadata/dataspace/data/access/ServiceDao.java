package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ServiceVersion;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:44:04 AM
 */
public interface ServiceDao extends Dao<Service>, RegistryDao<Service> {

    ServiceVersion getByVersion(String uriKey, String version);

}
