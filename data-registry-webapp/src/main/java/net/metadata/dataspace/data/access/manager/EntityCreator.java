package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.Subject;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:30:58 PM
 */
public interface EntityCreator {

    Record getNextRecord(Class clazz);

    Version getNextVersion(Record record);

    Subject getNextSubject();

}
