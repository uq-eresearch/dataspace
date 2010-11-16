package net.metadata.dataspace.data.model;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import net.metadata.dataspace.data.access.*;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.base.Subject;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import java.util.*;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:22:08 AM
 */
public class PopulatorUtil {
    private static EntityCreator entityCreator;
    private static DaoManager daoManager;

    public static Subject getSubject() throws Exception {
        Subject subject = entityCreator.getNextSubject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = uuid.toString();
        subject.setVocabulary(vocabUriString);
        subject.setValue("Test Subject");
        return subject;
    }

    public static CollectionVersion getCollectionVersion(Record collection) throws Exception {
        CollectionVersion collectionVersion = (CollectionVersion) entityCreator.getNextVersion(collection);
        collectionVersion.setParent(collection);
        collectionVersion.setTitle("Test Collection");
        collectionVersion.setContent("Test Collection Content");
        collectionVersion.setSummary("Test collection description");
        collectionVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Collection Author");
        collectionVersion.setAuthors(authors);
        collectionVersion.setLocation("http://test.location.com.au/collection");
        return collectionVersion;
    }

    public static PartyVersion getPartyVersion(Record party) throws Exception {
        PartyVersion partyVersion = (PartyVersion) entityCreator.getNextVersion(party);
        partyVersion.setParent(party);
        partyVersion.setTitle("Test Party Title");
        partyVersion.setSummary("Test Party Summary");
        partyVersion.setContent("Test Party Content");
        partyVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Party Author");
        partyVersion.setAuthors(authors);
        party.setUpdated(new Date());
        return partyVersion;
    }

    public static ServiceVersion getServiceVersion(Record service) {
        ServiceVersion serviceVersion = (ServiceVersion) entityCreator.getNextVersion(service);
        serviceVersion.setParent(service);
        serviceVersion.setTitle("Test Service Title");
        serviceVersion.setSummary("Test Service Summary");
        serviceVersion.setContent("Test Service Content");
        serviceVersion.setUpdated(new Date());
        serviceVersion.setLocation("http://test.location.com.au/collection");
        Set<String> authors = new HashSet<String>();
        authors.add("Test Service Author");
        serviceVersion.setAuthors(authors);
        return serviceVersion;
    }

    public static ActivityVersion getActivityVersion(Record activity) {
        ActivityVersion activityVersion = (ActivityVersion) entityCreator.getNextVersion(activity);
        activityVersion.setParent(activity);
        activityVersion.setTitle("Test Activity Title");
        activityVersion.setSummary("Test Activity Summary");
        activityVersion.setContent("Test Activity Content");
        activityVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Activity Author");
        activityVersion.setAuthors(authors);
        return activityVersion;
    }

    public static void cleanup() {
        PartyDao partyDao = daoManager.getPartyDao();
        PartyVersionDao partyVersionDao = daoManager.getPartyVersionDao();
        deleteEntity(partyDao, partyVersionDao);

        CollectionDao collectionDao = daoManager.getCollectionDao();
        CollectionVersionDao collectionVersionDao = daoManager.getCollectionVersionDao();
        deleteEntity(collectionDao, collectionVersionDao);

        ActivityDao activityDao = daoManager.getActivityDao();
        ActivityVersionDao activityVersionDao = daoManager.getActivityVersionDao();
        deleteEntity(activityDao, activityVersionDao);

        ServiceDao serviceDao = daoManager.getServiceDao();
        ServiceVersionDao serviceVersionDao = daoManager.getServiceVersionDao();
        deleteEntity(serviceDao, serviceVersionDao);
    }

    private static void deleteEntity(Dao parentDao, Dao versionDao) {
        List<Record> collectionList = parentDao.getAll();
        for (Record collection : collectionList) {
            SortedSet<Version> versions = collection.getVersions();
            for (Version version : versions) {
                if (parentDao instanceof PartyDao && versionDao instanceof PartyVersionDao) {
                    PartyVersion ver = (PartyVersion) version;
                    ver.getParticipantIn().removeAll(ver.getParticipantIn());
                    ver.getCollectorOf().removeAll(ver.getCollectorOf());
                    ver.getSubjects().removeAll(ver.getSubjects());
                }
                if (parentDao instanceof CollectionDao && versionDao instanceof CollectionVersionDao) {
                    CollectionVersion ver = (CollectionVersion) version;
                    ver.getOutputOf().removeAll(ver.getOutputOf());
                    ver.getSupports().removeAll(ver.getSupports());
                    ver.getCollector().removeAll(ver.getCollector());
                    ver.getSubjects().removeAll(ver.getSubjects());
                }
                if (parentDao instanceof ActivityDao && versionDao instanceof ActivityVersionDao) {
                    ActivityVersion ver = (ActivityVersion) version;
                    ver.getHasOutput().removeAll(ver.getHasOutput());
                    ver.getHasParticipant().removeAll(ver.getHasParticipant());
                }
                if (parentDao instanceof ServiceDao && versionDao instanceof ServiceVersionDao) {
                    ServiceVersion ver = (ServiceVersion) version;
                    ver.getSupportedBy().removeAll(ver.getSupportedBy());
                }
                version.getAuthors().removeAll(version.getAuthors());
                collection.getVersions().remove(version);
                versionDao.delete(version);
            }
            parentDao.delete(collection);
        }
    }

    public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

    public void setDaoManager(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }
}
