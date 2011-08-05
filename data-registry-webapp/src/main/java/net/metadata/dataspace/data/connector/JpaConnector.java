package net.metadata.dataspace.data.connector;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import java.io.Serializable;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:25:11 AM
 */
public class JpaConnector implements EntityManagerSource, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7163137476231779722L;
	private EntityManager em;
	
    public JpaConnector() {}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
    	this.em = em;
    }
    
}
