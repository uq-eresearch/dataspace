package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.FullNameDao;
import net.metadata.dataspace.data.model.context.FullName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 14/04/11
 * Time: 10:39 AM
 */
public class FullNameDaoImpl extends AbstractRegistryDao<FullName> implements FullNameDao, Serializable {

    public FullNameDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public List<FullName> getAllPublished() {
        return null;
    }

    @Override
    public List<FullName> getAllUnpublished() {
        return null;
    }

    @Override
    public List<FullName> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }
}