package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.context.Subject;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:55 AM
 */
public class SubjectDaoImpl extends AbstractRegistryDao<Subject> implements SubjectDao, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8981471485212513003L;

	public SubjectDaoImpl() {}
	
	public SubjectDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public List<Subject> getAllPublished() {
        return null;
    }

    @Override
    public List<Subject> getAllUnpublished() {
        return null;
    }


    @Override
    public List<Subject> getAllPublishedBetween(Date fromDate, Date untilDate) {
        return null;
    }

    @Override
    public Subject getSubject(String scheme, String term) {
        Query query = getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.term = :term AND o.isDefinedBy = :scheme");
        query.setParameter("scheme", scheme);
        query.setParameter("term", term);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }

    @Override
    public Subject getSubject(String scheme, String term, String label) {
        Query query = getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.term = :term AND o.isDefinedBy = :scheme AND o.label = :label");
        query.setParameter("scheme", scheme);
        query.setParameter("term", term);
        query.setParameter("label", label);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);
    }
}
