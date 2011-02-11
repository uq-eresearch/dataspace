package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;
import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.base.Service;
import net.metadata.dataspace.util.DateUtil;
import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;

import java.text.ParseException;
import java.util.*;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:11:34 PM
 */
public class RIFCSOaiCatalog extends AbstractCatalog {

    private Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RIFCSOaiCatalog() {
    }

    public RIFCSOaiCatalog(Properties properties) {
        this();
    }

    @Override
    public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
        logger.debug("listSets() is not implemented but being called");
        return null;
    }

    @Override
    public Map listSets(String s) throws BadResumptionTokenException, OAIInternalServerError {
        logger.debug("listSets() is not implemented but being called");
        return null;
    }

    @Override
    public Vector getSchemaLocations(String s) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
        logger.debug("getSchemaLocations() is not implemented but being called");
        return null;
    }

    @Override
    public Map<String, Iterator<String>> listIdentifiers(String from,
                                                         String until, String set, String metadataPrefix)
            throws BadArgumentException, CannotDisseminateFormatException,
            NoItemsMatchException, NoSetHierarchyException,
            OAIInternalServerError {
        logger.debug("Listing identifiers");
        Map<String, Iterator<String>> listIdentifiersMap = new HashMap<String, Iterator<String>>(2);
        Date fromDate = null;
        Date toDate = null;

        try {
            fromDate = DateUtil.parseDate(from, DateUtil.OAI_DATE_FORMATS);
            toDate = DateUtil.parseDate(until, DateUtil.OAI_DATE_FORMATS);
            //Offset timezone
            Date currentLocalDate = new Date();
            int offset = currentLocalDate.getTimezoneOffset();
            fromDate.setMinutes(fromDate.getMinutes() - offset);
            toDate.setMinutes(toDate.getMinutes() - offset);
        }
        catch (ParseException pe) {
            throw new BadArgumentException();
        }
        //TODO create queries to return records between the given dates
        List<Activity> activityList = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao().getAllPublishedBetween(fromDate, toDate);
        List<net.metadata.dataspace.data.model.base.Collection> collectionList = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getAllPublishedBetween(fromDate, toDate);
        List<Party> partyList = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao().getAllPublishedBetween(fromDate, toDate);
        List<Service> serviceList = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao().getAllPublishedBetween(fromDate, toDate);

        int size = activityList.size() + collectionList.size() + partyList.size() + serviceList.size();
        List<String> headers = new ArrayList<String>(size);
        List<String> identifiers = new ArrayList<String>(size);
        for (Activity activity : activityList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(activity.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(activity.getPublished()), getRecordFactory().getDatestamp(activity.getPublished()), getRecordFactory().getSetSpecs(activity), !activity.isActive())[0]);
        }
        for (net.metadata.dataspace.data.model.base.Collection collection : collectionList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(collection.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(collection.getPublished()), getRecordFactory().getDatestamp(collection.getPublished()), getRecordFactory().getSetSpecs(collection), !collection.isActive())[0]);
        }
        for (Party party : partyList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(party.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(party.getPublished()), getRecordFactory().getDatestamp(party.getPublished()), getRecordFactory().getSetSpecs(party), !party.isActive())[0]);
        }
        for (Service service : serviceList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(service.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(service.getPublished()), getRecordFactory().getDatestamp(service.getPublished()), getRecordFactory().getSetSpecs(service), !service.isActive())[0]);
        }
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        listIdentifiersMap.put("headers", headers.iterator());
        return listIdentifiersMap;
    }

    @Override
    public Map listIdentifiers(String s) throws BadResumptionTokenException, OAIInternalServerError {
        logger.debug("listIdentifiers() is not implemented but being called");
        return null;
    }

    @Override
    public String getRecord(String identifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        try {
            logger.debug("Getting a record");
            Record record = null;
            String key = AdapterHelper.getEntityID(identifier);
            if (identifier.contains(Constants.PATH_FOR_ACTIVITIES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getActivityDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_COLLECTIONS)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_PARTIES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getPartyDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_SERVICES)) {
                record = RegistryApplication.getApplicationContext().getDaoManager().getServiceDao().getByKey(key);
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
        logger.debug("close() is not implemented but being called");
    }

    public String toFinestFrom(String from) {
        return from;
    }

    public String toFinestUntil(String until) {
        return until;
    }
}
