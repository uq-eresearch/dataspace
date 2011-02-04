package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;
import net.metadata.dataspace.app.RegistryApplication;
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
        // Why? Ensures that any request for a metadataPrefix other than 'eml' gives appropriate 'unsupported format' error
        // We don't use the OAICat crosswalking, so in fact all we want is a non-null entry in the crosswalks map\
        // TODO: support other formats as needed
//		Map crosswalkMap = new HashMap();
//		crosswalkMap.put("eml", new CrosswalkItem("eml", "eml", "eml", "eml", 0));
//		crosswalks = new Crosswalks(crosswalkMap);
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

        List<net.metadata.dataspace.data.model.base.Collection> collectionList = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getAllPublished();
        List<String> headers = new ArrayList<String>(collectionList.size());
        List<String> identifiers = new ArrayList<String>(collectionList.size());
        for (net.metadata.dataspace.data.model.base.Collection col : collectionList) {
            identifiers.add(Long.toString(col.getId()));
            headers.add(RIFCSOaiRecordFactory.createHeader(col.getId().toString(), getRecordFactory().getDatestamp(col.getPublished()), getRecordFactory().getSetSpecs(col), false)[0]);
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
            net.metadata.dataspace.data.model.base.Collection col = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getAllPublished().get(0);
            String schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);
            return getRecordFactory().create(col, schemaURL, metadataPrefix);
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
