package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.model.User;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 4:28:44 PM
 */
public class UserDaoImpl extends JpaDao<User> implements UserDao, Serializable {
    public UserDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public User getByUsername(String username) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM AppUser o WHERE o.username = :username");
        query.setParameter("username", username);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "id should be unique";
        return (User) resultList.get(0);
    }
}
