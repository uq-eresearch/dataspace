package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.model.record.User;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 4:28:44 PM
 */
public class UserDaoImpl extends JpaDao<User> implements UserDao, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7737558958450055233L;

	public UserDaoImpl() {}
	
	public UserDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    @Transactional
    public User getByUsername(String username) {
        Query query = getEntityManager().createQuery("SELECT o FROM AppUser o WHERE o.username = :username");
        query.setParameter("username", username);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
