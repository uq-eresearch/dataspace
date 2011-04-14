package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.version.ActivityVersion;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:44:19 AM
 */
public interface ActivityDao extends RegistryDao<Activity> {

    ActivityVersion getByVersion(String uriKey, String version);

}
