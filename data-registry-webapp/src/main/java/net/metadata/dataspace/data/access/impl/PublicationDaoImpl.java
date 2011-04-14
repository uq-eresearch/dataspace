package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.PublicationDao;
import net.metadata.dataspace.data.model.context.Publication;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:08 PM
 */
public class PublicationDaoImpl extends AbstractRegistryDao<Publication> implements PublicationDao, Serializable {

    public PublicationDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
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

}
