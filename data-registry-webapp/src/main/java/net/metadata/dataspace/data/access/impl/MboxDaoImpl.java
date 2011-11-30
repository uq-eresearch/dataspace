package net.metadata.dataspace.data.access.impl;

import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.Query;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

import net.metadata.dataspace.data.access.MboxDao;
import net.metadata.dataspace.data.model.context.Mbox;

public class MboxDaoImpl extends JpaDao<Mbox> implements MboxDao {

	public MboxDaoImpl() {}

	public MboxDaoImpl(EntityManagerSource entityManagerSource) {
		super(entityManagerSource);
	}

    @Override
    public Mbox getByEmail(InternetAddress email) {
        Query query = getEntityManager().createQuery("FROM Mbox m where m.emailAddress = :email");
        query.setParameter("email", email.getAddress());
        @SuppressWarnings("unchecked")
		List<Mbox> resultList = (List<Mbox>) query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return resultList.get(0);
    }
}
