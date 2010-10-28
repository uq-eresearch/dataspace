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

    public static Collection getCollection() throws Exception {
        Collection collection = entityCreator.getNextCollection();
        collection.setTitle("Test Collection");
        collection.setContent("Test Collection Content");
        collection.setSummary("Test collection description");
        collection.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Collection Author");
        collection.setAuthors(authors);
        collection.setLocation("http://test.location.com.au/collection");
        return collection;
    }

    public static Party getParty() throws Exception {
        Party party = entityCreator.getNextParty();
        party.setTitle("Test Party Title");
        party.setSummary("Test Party Summary");
        party.setContent("Test Party Content");
        party.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Party Author");
        party.setAuthors(authors);
        return party;
    }

    public static Service getService() {
        Service service = entityCreator.getNextService();
        service.setTitle("Test Service Title");
        service.setSummary("Test Service Summary");
        service.setContent("Test Service Content");
        service.setUpdated(new Date());
        service.setLocation("http://test.location.com.au/collection");
        Set<String> authors = new HashSet<String>();
        authors.add("Test Service Author");
        service.setAuthors(authors);
        return service;
    }

    public static Activity getActivity() {
        Activity activity = entityCreator.getNextActivity();
        activity.setTitle("Test Activity Title");
        activity.setSummary("Test Activity Summary");
        activity.setContent("Test Activity Content");
        activity.setUpdated(new Date());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Activity Author");
        activity.setAuthors(authors);
        return activity;
    }


    public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

}
