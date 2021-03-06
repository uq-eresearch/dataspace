package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.ActivityVersionDao;
import net.metadata.dataspace.data.model.version.ActivityVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 09/11/2010
 * Time: 3:27:03 PM
 */
public class ActivityVersionDaoImpl extends JpaDao<ActivityVersion> implements ActivityVersionDao, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7725946239516023437L;
	
	public ActivityVersionDaoImpl() {}

	public ActivityVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
