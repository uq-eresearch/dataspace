package net.metadata.dataspace.model;

import java.net.URI;
import java.util.UUID;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:22:08 AM
 */
public class PopulatorUtil {

    public static Subject getSubject() throws Exception {
        Subject subject = new Subject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = "http://dataspace.uq.edu.au/collection/" + uuid.toString();
        URI vocabUri = new URI(vocabUriString);
        subject.setVocabulary(vocabUri);
        subject.setValue("Test Subject");
        return subject;
    }

    public static Collection getCollection() throws Exception {
        Collection collection = new Collection();
        UUID uuid = UUID.randomUUID();
        String collectionKey = "http://dataspace.uq.edu.au/collection/" + uuid.toString();
        collection.setKey(new URI(collectionKey));
        collection.setName("First Collection");
        collection.setDescription("Test collection description");

        return collection;
    }
}
