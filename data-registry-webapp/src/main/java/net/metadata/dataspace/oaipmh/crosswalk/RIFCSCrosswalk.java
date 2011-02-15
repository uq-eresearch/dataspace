package net.metadata.dataspace.oaipmh.crosswalk;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import net.metadata.dataspace.atom.util.AdapterHelper;
import net.metadata.dataspace.atom.writer.XSLTTransformerWriter;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.AgentVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;
import org.apache.abdera.model.Entry;
import org.apache.abdera.util.AbstractWriterOptions;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Author: alabri
 * Date: 04/02/2011
 * Time: 11:45:55 AM
 */
public class RIFCSCrosswalk extends Crosswalk {

    private Logger logger = Logger.getLogger(getClass());

    public RIFCSCrosswalk(Properties props) {
        super("http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd");
    }

    @Override
    public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
        try {
            logger.debug("Assembling a record");
            String xslFilePath = "";
            if (nativeItem instanceof ActivityVersion) {
                xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + Activity.class.getSimpleName().toLowerCase() + ".xsl";
            } else if (nativeItem instanceof CollectionVersion) {
                xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + Collection.class.getSimpleName().toLowerCase() + ".xsl";
            } else if (nativeItem instanceof AgentVersion) {
                xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + Agent.class.getSimpleName().toLowerCase() + ".xsl";
            } else if (nativeItem instanceof ServiceVersion) {
                xslFilePath = "/files/xslt/rifcs/atom2rifcs-" + Service.class.getSimpleName().toLowerCase() + ".xsl";
            }
            XSLTTransformerWriter writer = new XSLTTransformerWriter(xslFilePath);
            Entry entry = AdapterHelper.getEntryFromEntity((Version) nativeItem, true);
            AbstractWriterOptions writerOptions = new AbstractWriterOptions() {
            };
            writerOptions.setCharset("UTF8");
            Object object = writer.write(entry, writerOptions);
            String xmlContent = object.toString();
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
