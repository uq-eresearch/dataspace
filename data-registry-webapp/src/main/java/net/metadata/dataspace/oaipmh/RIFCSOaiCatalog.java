package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;
import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.base.Service;
import org.hibernate.ObjectNotFoundException;

import java.util.*;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:11:34 PM
 */
public class RIFCSOaiCatalog extends AbstractCatalog {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RIFCSOaiCatalog() {
    }

    public RIFCSOaiCatalog(Properties properties) {
        this();
    }

    @Override
    public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
        return null;
    }

    @Override
    public Map listSets(String s) throws BadResumptionTokenException, OAIInternalServerError {
        return null;
    }

    @Override
    public Vector getSchemaLocations(String s) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
        return null;
    }

    @Override
    public Map<String, Iterator<String>> listIdentifiers(String from,
                                                         String until, String set, String metadataPrefix)
            throws BadArgumentException, CannotDisseminateFormatException,
            NoItemsMatchException, NoSetHierarchyException,
            OAIInternalServerError {
        Map<String, Iterator<String>> listIdentifiersMap = new HashMap<String, Iterator<String>>(2);

        //TODO create queries to return records between the given dates
        List<Activity> activityList = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao().getAllPublished();
        List<net.metadata.dataspace.data.model.base.Collection> collectionList = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getAllPublished();
        List<Party> partyList = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao().getAllPublished();
        List<Service> serviceList = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao().getAllPublished();

        int size = activityList.size() + collectionList.size() + partyList.size() + serviceList.size();

        List<String> headers = new ArrayList<String>(size);
        List<String> identifiers = new ArrayList<String>(size);
        for (Activity activity : activityList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(activity.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(activity.getPublished()), getRecordFactory().getDatestamp(activity.getPublished()), getRecordFactory().getSetSpecs(activity), false)[0]);
        }
        for (net.metadata.dataspace.data.model.base.Collection collection : collectionList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(collection.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(collection.getPublished()), getRecordFactory().getDatestamp(collection.getPublished()), getRecordFactory().getSetSpecs(collection), false)[0]);
        }
        for (Party party : partyList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(party.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(party.getPublished()), getRecordFactory().getDatestamp(party.getPublished()), getRecordFactory().getSetSpecs(party), false)[0]);
        }
        for (Service service : serviceList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(service.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(service.getPublished()), getRecordFactory().getDatestamp(service.getPublished()), getRecordFactory().getSetSpecs(service), false)[0]);
        }
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        listIdentifiersMap.put("headers", headers.iterator());
        return listIdentifiersMap;
    }

    @Override
    public Map listIdentifiers(String s) throws BadResumptionTokenException, OAIInternalServerError {
        return null;
    }

    @Override
    public String getRecord(String identifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        try {
            Record record = null;
            if (identifier.contains(Constants.PATH_FOR_ACTIVITIES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao().getAllPublished().get(0);
            } else if (identifier.contains(Constants.PATH_FOR_COLLECTIONS)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getAllPublished().get(0);
            } else if (identifier.contains(Constants.PATH_FOR_PARTIES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao().getAllPublished().get(0);
            } else if (identifier.contains(Constants.PATH_FOR_SERVICES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao().getAllPublished().get(0);
            }
            if (record == null) {
                throw new IdDoesNotExistException(identifier);
            } else {
                String schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);
                return getRecordFactory().create(record.getPublished(), schemaURL, metadataPrefix);
            }
        } catch (ObjectNotFoundException e) {
            throw new IdDoesNotExistException(identifier);
        }
    }

    @Override
    public void close() {
    }

    public String toFinestFrom(String from) {
        return from;
    }

    public String toFinestUntil(String until) {
        return until;
    }
}
