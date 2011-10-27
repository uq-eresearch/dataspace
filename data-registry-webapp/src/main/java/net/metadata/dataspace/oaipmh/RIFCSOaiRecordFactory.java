package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.atom.util.AdapterOutputHelper;
import net.metadata.dataspace.data.model.Version;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:13:36 PM
 */
public class RIFCSOaiRecordFactory extends RecordFactory {

	private AdapterOutputHelper adapterOutputHelper = new AdapterOutputHelper();
    private Logger logger = Logger.getLogger(getClass());

    public RIFCSOaiRecordFactory(Properties properties) {
        super(properties);
    }

    @Override
    public String fromOAIIdentifier(String s) {
        logger.debug("fromOAIIdentifier() is not implemented but being called");
        return null;
    }

    @Override
    public String quickCreate(Object o, String s, String s1) throws IllegalArgumentException, CannotDisseminateFormatException {
        logger.debug("quickCreate() is not implemented but being called");
        return null;
    }

    @Override
    public String getOAIIdentifier(Object o) {
        try {
            return adapterOutputHelper.getEntryFromEntity((Version) o, true).getLink(Constants.REL_SELF).getHref().toString();
        } catch (ResponseContextException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDatestamp(Object o) {
//        return new SimpleDateFormat("yyyy-MM-dd").format(((Version) o).getUpdated());
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(((Version) o).getUpdated());
    }

    @Override
    public Iterator getSetSpecs(Object o) throws IllegalArgumentException {
        logger.debug("getSetSpecs() is not implemented but being called");
        return null;
    }

    @Override
    public boolean isDeleted(Object o) {
        return !((Version) o).getParent().isActive();
    }

    @Override
    public Iterator getAbouts(Object o) {
        logger.debug("getAbouts() is not implemented but being called");
        return null;
    }
}
