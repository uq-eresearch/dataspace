package net.metadata.dataspace.data.access;

import net.metadata.dataspace.data.access.impl.CollectionDaoImpl;
import net.metadata.dataspace.data.access.impl.PartyDaoImpl;
import net.metadata.dataspace.data.access.impl.SubjectDaoImpl;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 11:39:29 AM
 */
public class DaoRegister {

    private CollectionDaoImpl collectionDao;
    private PartyDaoImpl partyDao;
    private SubjectDaoImpl subjectDao;

    public void setCollectionDao(CollectionDaoImpl collectionDao) {
        this.collectionDao = collectionDao;
    }

    public CollectionDaoImpl getCollectionDao() {
        return collectionDao;
    }

    public void setPartyDao(PartyDaoImpl partyDao) {
        this.partyDao = partyDao;
    }

    public PartyDaoImpl getPartyDao() {
        return partyDao;
    }

    public void setSubjectDao(SubjectDaoImpl subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectDaoImpl getSubjectDao() {
        return subjectDao;
    }
}
