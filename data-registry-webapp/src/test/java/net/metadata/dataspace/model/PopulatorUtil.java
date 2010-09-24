package net.metadata.dataspace.model;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.app.DataRegistryApplicationConfiguration;

import java.util.Date;
import java.util.UUID;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:22:08 AM
 */
public class PopulatorUtil {

    private static DataRegistryApplicationConfiguration dataRegistryApplicationConfigurationImpl = DataRegistryApplication.getApplicationContext();

    public static Subject getSubject() throws Exception {
        Subject subject = new Subject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = dataRegistryApplicationConfigurationImpl.getUriPrefix() + "subject/" + uuid.toString();
        subject.setVocabulary(vocabUriString);
        subject.setValue("Test Subject");
        return subject;
    }

    public static Collection getCollection() throws Exception {
        Collection collection = new Collection();
        UUID uuid = UUID.randomUUID();
        collection.setTitle("Test Collection");

        collection.setSummary("Test collection description");
        UUID uuid2 = UUID.randomUUID();
        String collectionManagedBy = dataRegistryApplicationConfigurationImpl.getUriPrefix() + "collection/" + uuid2.toString();
        collection.setManagedBy(collectionManagedBy);
        collection.setLocation(collectionManagedBy);
        return collection;
    }

    public static Party getParty() {
        Party party = new Party();
        UUID uuid = UUID.randomUUID();
        party.setTitle("Test Party");
        party.setUpdated(new Date());
        party.setSummary("Test Party Description");
        UUID uuid2 = UUID.randomUUID();
        String collectionUri = dataRegistryApplicationConfigurationImpl.getUriPrefix() + "collection/" + uuid2.toString();
        party.setCollectorof(collectionUri);
        return party;
    }
}
