package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import net.metadata.dataspace.data.access.AgentVersionDao;
import net.metadata.dataspace.data.model.version.AgentVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 11:44:59 AM
 */

public class AgentVersionDaoImpl extends JpaDao<AgentVersion> implements AgentVersionDao, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7401872378377624847L;

	public AgentVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
