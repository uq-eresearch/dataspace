package net.metadata.dataspace.data.access.manager;

import net.metadata.dataspace.data.model.*;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 2:30:58 PM
 */
public interface EntityCreator {

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
