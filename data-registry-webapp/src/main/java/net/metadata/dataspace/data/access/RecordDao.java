package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;

public interface RecordDao<R extends Record<V>, V extends Version<R>> extends RegistryDao<R> {

    V getByVersion(String uriKey, String version);
	
    R getByOriginalId(String originalId);
}
