package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.CollectionVersionDao;
import net.metadata.dataspace.data.model.version.CollectionVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 09/11/2010
 * Time: 3:26:26 PM
 */
public class CollectionVersionDaoImpl extends JpaDao<CollectionVersion> implements CollectionVersionDao, Serializable {
    public CollectionVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}