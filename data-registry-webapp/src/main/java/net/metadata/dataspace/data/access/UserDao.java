package net.metadata.dataspace.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.model.record.User;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 4:28:07 PM
 */
public interface UserDao extends Dao<User> {
    User getByUsername(String username);
}
