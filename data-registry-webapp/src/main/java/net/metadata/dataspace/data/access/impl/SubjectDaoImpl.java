package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.model.Subject;

import java.io.Serializable;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:55 AM
 */
public class SubjectDaoImpl extends JpaDao<Subject> implements SubjectDao, Serializable {

    public SubjectDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

}
