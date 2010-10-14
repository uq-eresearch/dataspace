package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.model.Subject;
import net.metadata.dataspace.util.DaoHelper;

import java.io.Serializable;
import java.util.List;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:37:55 AM
 */
public class SubjectDaoImpl extends JpaDao<Subject> implements SubjectDao, Serializable {

    public SubjectDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }


    @Override
    public List<Subject> getAll() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.isActive = TRUE").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Subject getById(Long id) {
        List<?> resultList = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Subject o WHERE o.id = :id").setParameter("id", id).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (Subject) resultList.get(0);

    }

    @Override
    public void softDelete(String uriKey) {
        Long id = DaoHelper.fromOtherBaseToDecimal(31, uriKey).longValue();
        entityManagerSource.getEntityManager().createQuery("UPDATE Subject o SET o.isActive = FALSE WHERE o.id = :id").setParameter("id", id);
    }

}
