package net.metadata.dataspace.data.access;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;
import net.metadata.dataspace.model.Party;
import net.metadata.dataspace.model.PopulatorUtil;
import net.metadata.dataspace.model.Subject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: alabri
 * Date: 21/09/2010
 * Time: 3:23:30 PM
 */
public class PartyDaoImplTest {

    private DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    @Test
    public void testAddingParty() throws Exception {
        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);


        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();

        int originalPartyTableSize = partyDao.getAll().size();

        Party party = PopulatorUtil.getParty();
        List<Subject> subjects = new ArrayList<Subject>();
        subjects.add(subject);
        party.setSubjects(subjects);
        partyDao.save(party);

        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == (originalPartyTableSize + 1));
        assertEquals("Added and Retrieved collections are not the same.", party, partyDao.getById(party.getId()));
    }


    @Test
    public void testEditingParty() throws Exception {
        testAddingParty();

        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();
        //Collection table shouldn't be empty
        assertTrue("Collection table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);

        SubjectDao subjectDao = dataRegistryApplicationConfigurationImpl.getSubjectDao();
        Subject subject = PopulatorUtil.getSubject();
        subjectDao.save(subject);

        List<Party> partyList = partyDao.getAll();
        Party party = partyList.get(0);
        String id = party.getId();
        int originalSubjectListSize = party.getSubjects().size();
        party.getSubjects().add(subject);
        String originalCollectorOfUri = party.getCollectorOfURI();
        party.setCollectorOfURI(dataRegistryApplicationConfigurationImpl.getUriPrefix() + "collection/" + UUID.randomUUID().toString());

        partyDao.update(party);

        Party partyById = partyDao.getById(id);

        assertEquals("Modified and Retrieved parties are not the same", party, partyById);
        assertTrue("The number of subjects should increase Current: " + partyById.getSubjects().size() + " Expected: " + (originalSubjectListSize + 1), partyById.getSubjects().size() == (originalSubjectListSize + 1));
        assertFalse("Collector of URI should be updated " + originalCollectorOfUri + " Current: " + partyById.getCollectorOfURI(), originalCollectorOfUri.equals(partyById.getCollectorOfURI()));

    }


    @Test
    public void testRemovingCollection() throws Exception {
        testAddingParty();

        PartyDao partyDao = dataRegistryApplicationConfigurationImpl.getPartyDao();

        //Collection table shouldn't be empty
        assertTrue("Party table has " + partyDao.getAll().size() + " records", partyDao.getAll().size() != 0);
        //Remove all collections
        List<Party> partyList = partyDao.getAll();
        for (Party party : partyList) {
            partyDao.delete(party);
        }
        //Check that collection table has no records
        assertTrue("Party table should be empty. It has has " + partyDao.getAll().size() + " records", partyDao.getAll().size() == 0);
    }
}
