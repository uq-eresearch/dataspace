package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.base.Subject;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:16 AM
 */
public interface SubjectDao extends Dao<Subject>, RegistryDao<Subject> {

    Subject getSubject(String vocabulary, String value);
}
