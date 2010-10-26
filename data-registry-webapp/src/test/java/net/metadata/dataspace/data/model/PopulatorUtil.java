package net.metadata.dataspace.data.model;

import net.metadata.dataspace.data.sequencer.CollectionAtomicSequencer;
import net.metadata.dataspace.data.sequencer.PartyAtomicSequencer;
import net.metadata.dataspace.data.sequencer.SubjectAtomicSequencer;

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
    private static PartyAtomicSequencer partyAtomicSequencer;
    private static CollectionAtomicSequencer collectionAtomicSequencer;
    private static SubjectAtomicSequencer subjectAtomicSequencer;

    public static Subject getSubject() throws Exception {
        Subject subject = new Subject();
        UUID uuid = UUID.randomUUID();
        String vocabUriString = uuid.toString();
        subject.setAtomicNumber(subjectAtomicSequencer.next());
        subject.setVocabulary(vocabUriString);
        subject.setValue("Test Subject");
        return subject;
    }

    public static Collection getCollection() throws Exception {
        Collection collection = new Collection();

        collection.setTitle("Test Collection");
        collection.setContent("Test Collection Content");
        collection.setSummary("Test collection description");
        collection.setUpdated(new Date());
        collection.setAtomicNumber(collectionAtomicSequencer.next());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Collection Author");
        collection.setAuthors(authors);

        collection.setLocation("http://test.location.com.au/collection");

        return collection;
    }

    public static Party getParty() throws Exception {
        Party party = new Party();

        party.setTitle("Test Party Title");
        party.setSummary("Test Party Summary");
        party.setContent("Test Party Content");
        party.setUpdated(new Date());
        party.setAtomicNumber(partyAtomicSequencer.next());
        Set<String> authors = new HashSet<String>();
        authors.add("Test Party Author");
        party.setAuthors(authors);
        return party;
    }

    public void setPartyAtomicSequencer(PartyAtomicSequencer partyAtomicSequencer) {
        this.partyAtomicSequencer = partyAtomicSequencer;
    }

    public PartyAtomicSequencer getPartyAtomicSequencer() {
        return partyAtomicSequencer;
    }

    public void setCollectionAtomicSequencer(CollectionAtomicSequencer collectionAtomicSequencer) {
        this.collectionAtomicSequencer = collectionAtomicSequencer;
    }

    public CollectionAtomicSequencer getCollectionAtomicSequencer() {
        return collectionAtomicSequencer;
    }

    public void setSubjectAtomicSequencer(SubjectAtomicSequencer subjectAtomicSequencer) {
        this.subjectAtomicSequencer = subjectAtomicSequencer;
    }

    public SubjectAtomicSequencer getSubjectAtomicSequencer() {
        return subjectAtomicSequencer;
    }
}
