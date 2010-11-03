package net.metadata.dataspace.data.connector;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import org.restlet.resource.Representation;
import org.restlet.service.ConnectorService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 10:25:11 AM
 */
public class JpaConnector extends ConnectorService implements EntityManagerSource, Serializable {

    private final EntityManagerFactory emf;
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

    @Override
    public void afterSend(Representation entity) {
        EntityManager entityManager = entityManagerTL.get();
        if (entityManager != null) {
            entityManagerTL.remove();
            assert entityManager.isOpen() : "Entity manager should only be closed here but must have been closed elsewhere";
            entityManager.close();
        }
        super.afterSend(entity);
    }

}
