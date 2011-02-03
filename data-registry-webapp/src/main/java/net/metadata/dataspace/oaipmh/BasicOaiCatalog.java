package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.*;

import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:11:34 PM
 */
public class BasicOaiCatalog extends AbstractCatalog {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BasicOaiCatalog() {
        // Why? Ensures that any request for a metadataPrefix other than 'eml' gives appropriate 'unsupported format' error
        // We don't use the OAICat crosswalking, so in fact all we want is a non-null entry in the crosswalks map\
        // TODO: support other formats as needed
//		Map crosswalkMap = new HashMap();
//		crosswalkMap.put("eml", new CrosswalkItem("eml", "eml", "eml", "eml", 0));
//		crosswalks = new Crosswalks(crosswalkMap);
    }

    public BasicOaiCatalog(Properties properties) {
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
    public Map listIdentifiers(String s, String s1, String s2, String s3) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException, NoSetHierarchyException, OAIInternalServerError {
        return null;
    }

    @Override
    public Map listIdentifiers(String s) throws BadResumptionTokenException, OAIInternalServerError {
        return null;
    }

    @Override
    public String getRecord(String s, String s1) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
        return null;
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
