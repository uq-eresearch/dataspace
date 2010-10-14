package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.model.Subject;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:16 AM
 */
public interface SubjectDao extends Dao<Subject> {

    /**
     * Retrieves a subject by given id
     *
     * @param id of the subject
     * @return Subject
     */
    Subject getById(Long id);

    /**
     * Soft delete a subject by setting isActive flag to false
     *
     * @param uriKey 31 base uri key
     */
    void softDelete(String uriKey);
}
