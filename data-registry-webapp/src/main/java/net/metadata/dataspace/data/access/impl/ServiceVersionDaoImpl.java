package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.ServiceVersionDao;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 09/11/2010
 * Time: 3:26:45 PM
 */
public class ServiceVersionDaoImpl extends JpaDao<ServiceVersion> implements ServiceVersionDao, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -580421637426218277L;

	public ServiceVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
