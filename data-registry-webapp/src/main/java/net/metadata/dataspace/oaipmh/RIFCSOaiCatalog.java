package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;
import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.data.access.ActivityDao;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.CollectionDao;
import net.metadata.dataspace.data.access.ServiceDao;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.util.DateUtil;
import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;

import java.text.ParseException;
import java.util.*;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:11:34 PM
 */
public class RIFCSOaiCatalog extends AbstractCatalog {

    private Logger logger = Logger.getLogger(getClass());

    private ActivityDao activityDao;
    private AgentDao agentDao;
    private CollectionDao collectionDao;
    private ServiceDao serviceDao;


    public RIFCSOaiCatalog() {
    }

    public RIFCSOaiCatalog(Properties properties) {
        this();
    }

    @Override
    public Map<?, ?> listSets() throws NoSetHierarchyException, OAIInternalServerError {
        logger.debug("listSets() is not implemented but being called");
        return null;
    }

    @Override
    public Map<?, ?> listSets(String s) throws BadResumptionTokenException, OAIInternalServerError {
        logger.debug("listSets() is not implemented but being called");
        return null;
    }

    @Override
    public Vector<?> getSchemaLocations(String s) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
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

        Date fromDate = null;
        Date toDate = null;

        try {
            fromDate = DateUtil.parseDate(from, DateUtil.OAI_DATE_FORMATS);
            toDate = DateUtil.parseDate(until, DateUtil.OAI_DATE_FORMATS);
            logger.info("OAI ListIdentifiers parsed dates: " + fromDate + " to " + toDate);

            //Offset timezone
            long offset = Calendar.getInstance().getTimeZone().getRawOffset();

            fromDate.setTime(fromDate.getTime() - offset);
            toDate.setTime(toDate.getTime() - offset);

            /*Date currentLocalDate = new Date();
            int offset = currentLocalDate.getTimezoneOffset();
            fromDate.setMinutes(fromDate.getMinutes() - offset);
            toDate.setMinutes(toDate.getMinutes() - offset);*/
        } catch (ParseException pe) {
            throw new BadArgumentException();
        }

        /**
         * Only include the following on the OAI-PMH feed:
         * All published Collections
         * data creators (Agents)
         * data managers (Agents)
         * participants in an activity (Agents)
         * outPutOf (Activities) of Collections
         * accessedVia (Services) of Collections
         * */
        logger.info("Getting all published between " + fromDate + " and " + toDate);
        List<net.metadata.dataspace.data.model.record.Collection> collectionList = getCollectionDao().getAllPublishedBetween(fromDate, toDate);
        Set<Activity> uniqueActivitySet = new HashSet<Activity>();
        Set<Agent> uniqueAgentSet = new HashSet<Agent>();
        Set<Service> uniqueServiceSet = new HashSet<Service>();
        for (Collection collection : collectionList) {
            Set<Agent> creators = collection.getPublished().getCreators();
            if (!creators.isEmpty()) {
                uniqueAgentSet.addAll(creators);
            }
            Set<Agent> publishers = collection.getPublished().getPublishers();
            if (!publishers.isEmpty()) {
                uniqueAgentSet.addAll(publishers);
            }
            Set<Activity> activities = collection.getPublished().getOutputOf();
            if (!activities.isEmpty()) {
                uniqueActivitySet.addAll(activities);
            }
            for (AbstractRecordEntity<ActivityVersion> activity : activities) {
                Set<Agent> hasParticipants = activity.getPublished().getHasParticipants();
                uniqueAgentSet.addAll(hasParticipants);
            }
            Set<Service> services = collection.getPublished().getAccessedVia();
            if (!services.isEmpty()) {
                uniqueServiceSet.addAll(services);
            }
        }

        int size = uniqueActivitySet.size() + collectionList.size() + uniqueAgentSet.size() + uniqueServiceSet.size();
        if (size == 0) {
            throw new NoItemsMatchException();
        }

        List<String> headers = new ArrayList<String>(size);
        List<String> identifiers = new ArrayList<String>(size);

        for (AbstractRecordEntity<ActivityVersion> activity : uniqueActivitySet) {
            identifiers.add(getRecordFactory().getOAIIdentifier(activity.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(activity.getPublished()), getRecordFactory().getDatestamp(activity.getPublished()), getRecordFactory().getSetSpecs(activity), !activity.isActive())[0]);
        }
        for (net.metadata.dataspace.data.model.record.Collection collection : collectionList) {
            identifiers.add(getRecordFactory().getOAIIdentifier(collection.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(collection.getPublished()), getRecordFactory().getDatestamp(collection.getPublished()), getRecordFactory().getSetSpecs(collection), !collection.isActive())[0]);
        }
        for (Agent agent : uniqueAgentSet) {
            identifiers.add(getRecordFactory().getOAIIdentifier(agent.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(agent.getPublished()), getRecordFactory().getDatestamp(agent.getPublished()), getRecordFactory().getSetSpecs(agent), !agent.isActive())[0]);
        }
        for (Service service : uniqueServiceSet) {
            identifiers.add(getRecordFactory().getOAIIdentifier(service.getPublished()));
            headers.add(RIFCSOaiRecordFactory.createHeader(getRecordFactory().getOAIIdentifier(service.getPublished()), getRecordFactory().getDatestamp(service.getPublished()), getRecordFactory().getSetSpecs(service), !service.isActive())[0]);
        }

        Map<String, Iterator<String>> listIdentifiersMap = new HashMap<String, Iterator<String>>(2);
        listIdentifiersMap.put("identifiers", identifiers.iterator());
        listIdentifiersMap.put("headers", headers.iterator());
        return listIdentifiersMap;
    }

    @Override
    public Map<?, ?> listIdentifiers(String s) throws BadResumptionTokenException, OAIInternalServerError {
        logger.debug("listIdentifiers() is not implemented but being called");
        return null;
    }

    @Override
    public String getRecord(String identifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        try {
            logger.debug("Getting a record");
            Record record = null;
            String key = OperationHelper.getEntityID(identifier);
            if (identifier.contains(Constants.PATH_FOR_ACTIVITIES)) {
                record = getActivityDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_COLLECTIONS)) {
                record = getCollectionDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_AGENTS)) {
                record = getAgentDao().getByKey(key);
            } else if (identifier.contains(Constants.PATH_FOR_SERVICES)) {
                record = getServiceDao().getByKey(key);
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

	public ActivityDao getActivityDao() {
		return activityDao;
	}

	@Required
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	@Required
	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	@Required
	public void setCollectionDao(CollectionDao collectionDao) {
		this.collectionDao = collectionDao;
	}

	public ServiceDao getServiceDao() {
		return serviceDao;
	}

	@Required
	public void setServiceDao(ServiceDao serviceDao) {
		this.serviceDao = serviceDao;
	}
}
