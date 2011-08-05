package net.metadata.dataspace.util;

import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.SubjectDao;
import net.metadata.dataspace.data.model.context.Subject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Author: alabri
 * Date: 12/05/11
 * Time: 11:04 AM
 */
public class ANZSRCLoader {

    private static final String ELEMENT_DESCRIPTION = "rdf:Description";
    private static final String ATTRIBUTE_ABOUT = "rdf:about";
    private static final String ELEMENT_LABEL = "skos:prefLabel";

    private static enum code {
        FOR, SEO, TOA
    }

    public static boolean loadANZSRCCodes() {
        try {
            URL resourceAsStream = null;
            String rdfFile = "";
            String scheme = "";
            code[] values = code.values();
            for (code codeValue : values) {
                if (codeValue.equals(code.FOR)) {
                    rdfFile = "/files/other/for.rdf";
                    scheme = "http://purl.org/anzsrc/for";
                } else if (codeValue.equals(code.SEO)) {
                    rdfFile = "/files/other/seo.rdf";
                    scheme = "http://purl.org/anzsrc/seo";
                } else {
                    rdfFile = "/files/other/toa.rdf";
                    scheme = "http://purl.org/anzsrc/toa";
                }
                resourceAsStream = ANZSRCLoader.class.getResource(rdfFile);
                if (resourceAsStream == null) {
                    throw new Exception("RDF file '" + rdfFile + "' not found, please ensure it is on the classpath");
                }
                File codeFile = new File(resourceAsStream.getPath());
                Document dom = getDOM(codeFile);
                dom.getDocumentElement().normalize();

                SubjectDao subjectDao = RegistryApplication.getApplicationContext().getDaoManager().getSubjectDao();
                NodeList nodeList = dom.getElementsByTagName(ELEMENT_DESCRIPTION);
                EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getEntityManagerSource().getEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element descriptionElement = (Element) nodeList.item(i);
                    String label = descriptionElement.getElementsByTagName(ELEMENT_LABEL).item(0).getTextContent();
                    if (label.contains(" - ")) {
                        int pos = label.indexOf(" - ");
                        label = label.substring(pos + 3).trim();
                    }
                    String term = descriptionElement.getAttribute(ATTRIBUTE_ABOUT).replace("/#", "#");
                    if (subjectDao.getSubject(scheme, term, label) == null) {
                        Subject subject = RegistryApplication.getApplicationContext().getEntityCreator().getNextSubject();
                        subject.setDefinedBy(scheme);
                        subject.setTerm(term);
                        subject.setLabel(label);
                        entityManager.persist(subject);
                    }
                }
                transaction.commit();
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private static Document getDOM(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }
}
