package net.metadata.dataspace.atom.util;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.net.URL;

/**
 * Author: alabri
 * Date: 07/02/2011
 * Time: 4:56:00 PM
 */
public class SchemaHelper {

    public static boolean isValidRIFCS(Source source, String entryLocation) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL resource = SchemaHelper.class.getResource("/files/schema/registryObjects.xsd");
        String path = resource.getPath();
        File schemaLocation = new File(path);
        Schema schema = factory.newSchema(schemaLocation);
        Validator validator = schema.newValidator();
        try {
            validator.validate(source);
            System.out.println(entryLocation + " is valid.");
            return true;
        }
        catch (SAXException ex) {
            System.out.println(entryLocation + " is not valid because ");
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
