package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.model.context.Subject;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:16 AM
 */
public interface SubjectDao extends RegistryDao<Subject> {

    Subject getSubject(String scheme, String term);

    Subject getSubject(String scheme, String term, String label);
}
