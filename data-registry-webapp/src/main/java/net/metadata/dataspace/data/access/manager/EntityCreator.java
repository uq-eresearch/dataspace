package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.model.Context;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:30:58 PM
 */
public interface EntityCreator {

    <R extends Record<?>> R getNextRecord(Class<R> clazz);

    Context getNextResource(Class<?> clazz);

    <R extends Record<?>, V extends Version<?>> V getNextVersion(R record);

    Subject getNextSubject();

    Publication getNextPublication();

    Source getNextSource();

    FullName getFullName();

}
