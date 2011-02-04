package net.metadata.dataspace.oaipmh.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

import java.util.Properties;

/**
 * Author: alabri
 * Date: 04/02/2011
 * Time: 11:47:40 AM
 */
public class DCCrosswalk extends Crosswalk {

    public DCCrosswalk(Properties props) {
        super("http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
    }

    @Override
    public String createMetadata(Object o) throws CannotDisseminateFormatException {
        return null;
    }

    @Override
    public boolean isAvailableFor(Object o) {
        return true;
    }
}
