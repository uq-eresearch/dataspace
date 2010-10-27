package net.metadata.dataspace.util;

import net.metadata.dataspace.data.model.Collection;
import net.metadata.dataspace.data.model.Party;
import net.metadata.dataspace.data.model.Subject;
import net.metadata.dataspace.data.sequencer.*;

/**
 * User: alabri
 * Date: 01/10/2010
 * Time: 2:51:41 PM
 */
public class DaoHelper {

    private static final String baseDigits = "0123456789bcdfghjklmnpqrstvwxyz";
    private static PartyAtomicSequencer partyAtomicSequencer;
    private static CollectionAtomicSequencer collectionAtomicSequencer;
    private static SubjectAtomicSequencer subjectAtomicSequencer;
    private ServiceAtomicSequencer serviceAtomicSequencer;
    private ActivityAtomicSequencer activityAtomicSequencer;

    public static String fromDecimalToOtherBase(int base, int decimalNumber) {
        String tempVal = decimalNumber == 0 ? "0" : "";
        int mod = 0;

        while (decimalNumber != 0) {
            mod = decimalNumber % base;
            tempVal = baseDigits.substring(mod, mod + 1) + tempVal;
            decimalNumber = decimalNumber / base;
        }
        return tempVal;
    }


    public static Integer fromOtherBaseToDecimal(int base, String number) {
        int iterator = number.length();
        int returnValue = 0;
        int multiplier = 1;

        while (iterator > 0) {
            returnValue = returnValue + (baseDigits.indexOf(number.substring(iterator - 1, iterator)) * multiplier);
            multiplier = multiplier * base;
            --iterator;
        }
        return returnValue;
    }

    public static Party getNextParty() {
        Party party = new Party();
        party.setAtomicNumber(partyAtomicSequencer.next());
        return party;
    }

    public static Collection getNextCollection() {
        Collection collection = new Collection();
        collection.setAtomicNumber(collectionAtomicSequencer.next());
        return collection;
    }

    public static Subject getNextSubject() {
        Subject subject = new Subject();
        subject.setAtomicNumber(subjectAtomicSequencer.next());
        return subject;
    }

    public void setPartyAtomicSequencer(PartyAtomicSequencer partyAtomicSequencer) {
        DaoHelper.partyAtomicSequencer = partyAtomicSequencer;
    }

    public PartyAtomicSequencer getPartyAtomicSequencer() {
        return partyAtomicSequencer;
    }

    public void setCollectionAtomicSequencer(CollectionAtomicSequencer collectionAtomicSequencer) {
        DaoHelper.collectionAtomicSequencer = collectionAtomicSequencer;
    }

    public CollectionAtomicSequencer getCollectionAtomicSequencer() {
        return collectionAtomicSequencer;
    }

    public void setSubjectAtomicSequencer(SubjectAtomicSequencer subjectAtomicSequencer) {
        DaoHelper.subjectAtomicSequencer = subjectAtomicSequencer;
    }

    public SubjectAtomicSequencer getSubjectAtomicSequencer() {
        return subjectAtomicSequencer;
    }

    public void setServiceAtomicSequencer(ServiceAtomicSequencer serviceAtomicSequencer) {
        this.serviceAtomicSequencer = serviceAtomicSequencer;
    }

    public ServiceAtomicSequencer getServiceAtomicSequencer() {
        return serviceAtomicSequencer;
    }

    public void setActivityAtomicSequencer(ActivityAtomicSequencer activityAtomicSequencer) {
        this.activityAtomicSequencer = activityAtomicSequencer;
    }

    public ActivityAtomicSequencer getActivityAtomicSequencer() {
        return activityAtomicSequencer;
    }
}
