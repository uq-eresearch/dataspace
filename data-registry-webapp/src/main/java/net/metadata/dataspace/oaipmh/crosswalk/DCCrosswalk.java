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
        try {

            String xmlContent = "<warning>The DC Crosswalk is not supported by this repository.</warning>";
            // need to strip the xml declaration - <?xml version="1.0"
            // encoding="UTF-16"?>
            if (xmlContent.startsWith("<?xml ")) {
                xmlContent = xmlContent.substring(xmlContent.indexOf("?>") + 2);
            }
            return xmlContent;
        } catch (Exception e) {
            throw new CannotDisseminateFormatException(e.toString());
        }
    }

    @Override
    public boolean isAvailableFor(Object o) {
        return false;
    }
}
