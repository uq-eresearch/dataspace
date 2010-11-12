package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PartyVersionDao;
import net.metadata.dataspace.data.model.version.PartyVersion;

import java.io.Serializable;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 11:44:59 AM
 */

public class PartyVersionDaoImpl extends JpaDao<PartyVersion> implements PartyVersionDao, Serializable {

    public PartyVersionDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
