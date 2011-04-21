package net.metadata.dataspace.data.connector;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:25:11 AM
 */
public class JpaConnector implements EntityManagerSource, Serializable {

    private EntityManagerFactory emf;
    private final ThreadLocal<EntityManager> entityManagerTL = new ThreadLocal<EntityManager>();

    public JpaConnector(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerTL.get();
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManagerTL.set(entityManager);
        }
        return entityManager;
    }
}
