package net.metadata.dataspace.data.access.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import au.edu.uq.itee.maenad.dataaccess.Page;
import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

public class JpaDao<T> implements Dao<T> {

	protected EntityManagerSource entityManagerSource;

	public JpaDao() {
		
	}
	
	public JpaDao(EntityManagerSource entityManagerSource) {
    	this.setEntityManagerSource(entityManagerSource);
	}

	
	public EntityManagerSource getEntityManagerSource() {
		return entityManagerSource;
	}

	public void setEntityManagerSource(EntityManagerSource entityManagerSource) {
		this.entityManagerSource = entityManagerSource;
	}
	
	@Override
	@Transactional
	public T load(Object id) {
		@SuppressWarnings("unchecked")
		Class<T> actualClassParameter = (Class<T>) ((ParameterizedType) this
				.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		EntityManager entityManager = entityManagerSource.getEntityManager();
		T result = entityManager.find(actualClassParameter, id);
		if (result != null) {
			entityManager.refresh(result);
		}
		return result;
	}

	@Override
	@Transactional
	public void save(T object) {
		entityManagerSource.getEntityManager().persist(object);
	}

	@Override
	@Transactional
	public T update(T object) {
		T result = entityManagerSource.getEntityManager().merge(object);
		return result;
	}

	@Override
	@Transactional
	public void delete(T object) {
		entityManagerSource.getEntityManager().remove(object);
	}

	@Override
	@Transactional
	public void refresh(T object) {
		entityManagerSource.getEntityManager().refresh(object);
	}

	@Override
	@Transactional
	public List<T> getAll() {
		@SuppressWarnings("unchecked")
		List<T> result = getQueryForAll().getResultList();
		return result;
	}

	@Override
	@Transactional
	public Page<T> getPage(int offset, int limit) {
		Query query = getQueryForAll();
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		@SuppressWarnings("unchecked")
		List<T> result = query.getResultList();

		// TODO: the result of count(*) can be either an Integer or Long,
		// depending on JDBC driver
		// we should really treat it as Long to be on the safe side
		int count = ((Number) entityManagerSource
				.getEntityManager()
				.createQuery(
						"SELECT COUNT(o) " + "FROM " + getEntityName() + " o")
				.getSingleResult()).intValue();
		return new Page<T>(result, offset, limit, count);
	}

	protected Query getQueryForAll() {
		Class<T> actualClassParameter = getActualClassParameter();
		String orderExpression = "";
		try {
			if (actualClassParameter.getField("id") != null) {
				orderExpression = "ORDER BY id";
			}
		} catch (Exception ex) {
			// we just don't have that field (or aren't allowed reflection)
		}
		Query query = entityManagerSource.getEntityManager().createQuery(
				"SELECT o " + "FROM " + getEntityName() + " o "
						+ orderExpression);
		return query;
	}
	
    protected String getEntityName() {
        Class<T> actualClassParameter = getActualClassParameter();
        Entity annotation = actualClassParameter.getAnnotation(Entity.class);
        String tableName = (annotation.name() != null) && (annotation.name().length() > 0) ? annotation.name() : actualClassParameter.getSimpleName();
        return tableName;
    }

    protected Class<T> getActualClassParameter() {
        @SuppressWarnings("unchecked")
        Class<T> actualClassParameter = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return actualClassParameter;
    }
    
    protected EntityManager getEntityManager() {
    	return this.entityManagerSource.getEntityManager();
    }

}