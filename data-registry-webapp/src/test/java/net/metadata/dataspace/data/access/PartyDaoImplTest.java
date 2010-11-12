package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.connector.JpaConnector;
import net.metadata.dataspace.data.model.PopulatorUtil;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.version.PartyVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:23:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
public class PartyDaoImplTest {

    @Autowired
    private CollectionDao collectionDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private PartyDao partyDao;
    @Autowired
    private EntityCreator entityCreator;

    @Autowired
    private JpaConnector jpaConnector;

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PopulatorUtil.cleanup();
        entityManager = jpaConnector.getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        //Remove all parties
        PopulatorUtil.cleanup();
    }

    @Test
    public void testAddingParty() throws Exception {
        Party party = entityCreator.getNextParty();
        party.setUpdated(new Date());
        entityManager.getTransaction().begin();
        int originalTableSize = partyDao.getAll().size();
        PartyVersion partyVersion = PopulatorUtil.getPartyVersion(party);
        partyVersion.getSubjects().add(PopulatorUtil.getSubject());
        partyVersion.getSubjects().add(PopulatorUtil.getSubject());
        party.getVersions().add(partyVersion);
        entityManager.persist(partyVersion);
        entityManager.persist(party);
        entityManager.getTransaction().commit();

        Long id = party.getId();
        Party partyById = partyDao.getById(id);
        assertTrue("Table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == (originalTableSize + 1));
        assertEquals("Added and Retrieved records are not the same.", id, partyById.getId());
        assertEquals("Number of versions", 1, partyById.getVersions().size());
        assertEquals("Number of subjects", 2, partyById.getVersions().first().getSubjects().size());
    }


    @Test
    public void testEditingParty() throws Exception {
        testAddingParty();
        assertTrue("Table is empty", partyDao.getAll().size() != 0);
        List<Party> partyList = partyDao.getAll();
        Party party = partyList.get(0);
        entityManager.getTransaction().begin();
        Long id = party.getId();
        Date now = new Date();
        String summary = "Updated Summary";
        party.getVersions().first().setSummary(summary);
        party.setUpdated(now);
        entityManager.merge(party);
        entityManager.getTransaction().commit();
        Party partyById = partyDao.getById(id);
        assertEquals("Modified and Retrieved parties are not the same", party, partyById);
        assertEquals("Update Date was not updated", now, partyById.getUpdated());
        assertEquals("Summary was not updated", summary, partyById.getVersions().first().getSummary());
    }

    @Test
    public void testRemovingParty() throws Exception {
        testAddingParty();
        assertTrue("Table is empty", partyDao.getAll().size() != 0);
        List<Party> partyList = partyDao.getAll();
        for (Party party : partyList) {
            partyDao.delete(party);
        }
        assertTrue("Table is not empty", partyDao.getAll().size() == 0);
    }

    @Test
    public void testSoftDeleteParty() throws Exception {
        testAddingParty();
        assertTrue("Table is empty", partyDao.getAll().size() != 0);
        List<Party> partyList = partyDao.getAll();
        int updated = 0;
        for (Party party : partyList) {
            updated = partyDao.softDelete(party.getUriKey());
            partyDao.refresh(party);
        }
        assertEquals("Updated rows", 1, updated);
        assertTrue("Table is empty", partyDao.getAll().size() != 0);
        assertEquals("Table has active records", 0, partyDao.getAllActive().size());
    }
}
