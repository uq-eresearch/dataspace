package net.metadata.dataspace.atom.util;

import net.metadata.dataspace.atom.AtomNamespaceContext;
import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Author: alabri
 * Date: 21/01/2011
 * Time: 11:47:18 AM
 */
public class XPathHelper {

    public XPathHelper() {
    }

    public static XPath getXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NamespaceContext ctx = new AtomNamespaceContext();
        xpath.setNamespaceContext(ctx);
        return xpath;
    }

    public static Document getDocFromStream(InputStream inputStream) throws Exception {
        DocumentBuilderFactory xmlFact = DocumentBuilderFactory.newInstance();
        xmlFact.setNamespaceAware(true);
        DocumentBuilder builder = xmlFact.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        return doc;
    }

    public static Document getDocFromFile(String pathToFile) throws Exception {
        URL resource = XPathHelper.class.getResource(pathToFile);
        String path = resource.getPath();
        File inputFile = new File(path);
        DocumentBuilderFactory xmlFact = DocumentBuilderFactory.newInstance();
        xmlFact.setNamespaceAware(true);
        DocumentBuilder builder = xmlFact.newDocumentBuilder();
        Document doc = builder.parse(inputFile);
        return doc;
    }
}
