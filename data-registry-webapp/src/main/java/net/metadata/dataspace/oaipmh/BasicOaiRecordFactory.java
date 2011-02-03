package net.metadata.dataspace.oaipmh;

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

import java.util.Iterator;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 4:13:36 PM
 */
public class BasicOaiRecordFactory extends RecordFactory {

    public BasicOaiRecordFactory(Properties properties) {
        super(properties);
    }

    public BasicOaiRecordFactory() {
        this(new Properties());
    }

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
        return null;
    }

    @Override
    public String getDatestamp(Object o) {
        return null;
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
