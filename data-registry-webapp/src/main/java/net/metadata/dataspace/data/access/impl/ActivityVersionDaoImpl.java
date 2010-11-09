package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.ActivityVersionDao;
import net.metadata.dataspace.data.model.ActivityVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 09/11/2010
 * Time: 3:27:03 PM
 */
public class ActivityVersionDaoImpl extends JpaDao<ActivityVersion> implements ActivityVersionDao, Serializable {
    public ActivityVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
