package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.model.resource.Source;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:21 PM
 */
public class SourceDaoImpl extends JpaDao<Source> implements SourceDao, Serializable {

    public SourceDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Source getById(Long id) {
        return null;
    }

    @Override
    public Source getByKey(String uriKey) {
        return null;
    }

    @Override
    public int softDelete(String uriKey) {
        return 0;
    }

    @Override
    public List<Source> getAllActive() {
        return null;
    }

    @Override
    public List<Source> getAllInactive() {
        return null;
    }

    @Override
    public List<Source> getAllPublished() {
        return null;
    }

    @Override
    public List<Source> getAllUnpublished() {
        return null;
    }

    @Override
    public List<Source> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Source getMostRecentUpdated() {
        return null;
    }

    @Override
    public Source getMostRecentInserted() {
        return null;
    }
}
