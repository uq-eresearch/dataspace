package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:30:58 PM
 */
public interface EntityCreator {

    Record getNextRecord(Class clazz);

    Version getNextVersion(Record record);

    //TODO: Remove these methods from the interface and make them private in the impl

    Party getNextParty();

    PartyVersion getNextPartyVersion(Party party);

    Collection getNextCollection();

    CollectionVersion getNextCollectionVersion(Collection collection);

    Subject getNextSubject();

    Service getNextService();

    ServiceVersion getNextServiceVersion(Service service);

    Activity getNextActivity();

    ActivityVersion getNextActivityVersion(Activity activity);
}
