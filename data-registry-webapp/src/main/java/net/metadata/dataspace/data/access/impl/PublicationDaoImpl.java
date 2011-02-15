package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PublicationDao;
import net.metadata.dataspace.data.model.resource.Publication;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:08 PM
 */
public class PublicationDaoImpl extends JpaDao<Publication> implements PublicationDao, Serializable {

    public PublicationDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public Publication getById(Long id) {
        return null;
    }

    @Override
    public Publication getByKey(String uriKey) {
        return null;
    }

    @Override
    public int softDelete(String uriKey) {
        return 0;
    }

    @Override
    public List<Publication> getAllActive() {
        return null;
    }

    @Override
    public List<Publication> getAllInactive() {
        return null;
    }

    @Override
    public List<Publication> getAllPublished() {
        return null;
    }

    @Override
    public List<Publication> getAllUnpublished() {
        return null;
    }

    @Override
    public List<Publication> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Publication getMostRecentUpdated() {
        return null;
    }

    @Override
    public Publication getMostRecentInserted() {
        return null;
    }
}
