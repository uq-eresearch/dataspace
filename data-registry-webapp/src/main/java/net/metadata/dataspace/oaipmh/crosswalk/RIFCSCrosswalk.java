package net.metadata.dataspace.oaipmh.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

import java.util.Properties;

/**
 * Author: alabri
 * Date: 04/02/2011
 * Time: 11:45:55 AM
 */
public class RIFCSCrosswalk extends Crosswalk {

    public RIFCSCrosswalk(Properties props) {
        super("http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd");
    }

    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        try {
            String xmlContent = "<test>Testing</test>";

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
        return true;
    }
}
