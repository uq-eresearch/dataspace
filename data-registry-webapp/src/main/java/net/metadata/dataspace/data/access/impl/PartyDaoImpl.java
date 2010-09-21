package net.metadata.dataspace.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import net.metadata.dataspace.data.access.PartyDao;
import net.metadata.dataspace.model.Party;

import java.io.Serializable;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 1:22:12 PM
 */
public class PartyDaoImpl extends JpaDao<Party> implements PartyDao, Serializable {
    public PartyDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }
}
