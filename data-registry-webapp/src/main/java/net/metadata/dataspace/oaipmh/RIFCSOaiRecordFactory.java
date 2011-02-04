package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.data.model.Version;
import org.apache.abdera.protocol.server.context.ResponseContextException;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:13:36 PM
 */
public class RIFCSOaiRecordFactory extends RecordFactory {

    public RIFCSOaiRecordFactory(Properties properties) {
        super(properties);
    }

//    public RIFCSOaiRecordFactory() {
//
//        this();
//    }

    @Override
    public String fromOAIIdentifier(String s) {
        return null;
    }

    @Override
    public String quickCreate(Object o, String s, String s1) throws IllegalArgumentException, CannotDisseminateFormatException {
        return null;
    }

    @Override
    public String getOAIIdentifier(Object o) {
        try {
            return AdapterHelper.getEntryFromEntity((Version) o, true).getId().toString();
        } catch (ResponseContextException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDatestamp(Object o) {
        return new SimpleDateFormat("yyyy-MM-dd").format(((Version) o).getUpdated());
    }

    @Override
    public Iterator getSetSpecs(Object o) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean isDeleted(Object o) {
        return false;
    }

    @Override
    public Iterator getAbouts(Object o) {
        return null;
    }
}
