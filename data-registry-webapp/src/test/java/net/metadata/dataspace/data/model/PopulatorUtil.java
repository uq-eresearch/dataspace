package net.metadata.dataspace.data.model;

import net.metadata.dataspace.data.access.manager.EntityCreator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:22:08 AM
 */
public class PopulatorUtil {
    private static EntityCreator entityCreator;

    public static Subject getSubject() throws Exception {
        Subject subject = entityCreator.getNextSubject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = uuid.toString();
        subject.setVocabulary(vocabUriString);
        subject.setValue("Test Subject");
        return subject;
    }

    public static CollectionVersion getCollectionVersion(Collection collection) throws Exception {
        CollectionVersion collectionVersion = entityCreator.getNextCollectionVersion(collection);
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

    public static PartyVersion getPartyVersion(Party party) throws Exception {
        PartyVersion partyVersion = entityCreator.getNextPartyVersion(party);
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

    public static ServiceVersion getServiceVersion(Service service) {
        ServiceVersion serviceVersion = entityCreator.getNextServiceVersion(service);
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

    public static ActivityVersion getActivityVersion(Activity activity) {
        ActivityVersion activityVersion = entityCreator.getNextActivityVersion(activity);
        activityVersion.setTitle("Test Activity Title");
        activityVersion.setSummary("Test Activity Summary");
        activityVersion.setContent("Test Activity Content");
        activityVersion.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Activity Author");
        activityVersion.setAuthors(authors);
        return activityVersion;
    }


    public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

}
