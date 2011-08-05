package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.model.context.Source;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 1:47:21 PM
 */
public class SourceDaoImpl extends AbstractRegistryDao<Source> implements SourceDao, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1536020048285540373L;

	public SourceDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
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
    public Source getBySourceURI(String sourceUri) {
        Query query = getEntityManager().createQuery("SELECT o FROM Source o WHERE o.sourceURI = :sourceUri");
        query.setParameter("sourceUri", sourceUri);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Source) resultList.get(0);
    }
}
