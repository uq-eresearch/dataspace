package net.metadata.dataspace.oaipmh.verb;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalks;
import ORG.oclc.oai.server.verb.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 10/02/2011
 * Time: 3:56:47 PM
 */
public class OAIGetRecord extends ServerVerb {
    private static Logger logger = Logger.getLogger(OAIGetRecord.class);
    private static ArrayList<String> validParamNames = new ArrayList<String>();

    static {
        validParamNames.add("verb");
        validParamNames.add("identifier");
        validParamNames.add("metadataPrefix");
    }

    /**
     * Construct the xml response on the server-side.
     *
     * @param context the servlet context
     * @param request the servlet request
     * @return a String containing the XML response
     * @throws ORG.oclc.oai.server.verb.OAIInternalServerError
     *          an http 500 status error occurred
     */
    public static String construct(HashMap<?, ?> context,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Transformer serverTransformer)
            throws OAIInternalServerError, TransformerException {
        Properties properties = (Properties) context.get("OAIHandler.properties");
        AbstractCatalog abstractCatalog =
                (AbstractCatalog) context.get("OAIHandler.catalog");
        String baseURL = properties.getProperty("OAIHandler.baseURL");
        if (baseURL == null) {
            try {
                baseURL = request.getRequestURL().toString();
            } catch (java.lang.NoSuchMethodError f) {
                baseURL = HttpUtils.getRequestURL(request).toString();
            }
        }
        StringBuffer sb = new StringBuffer();
        String identifier = request.getParameter("identifier");
        String metadataPrefix = request.getParameter("metadataPrefix");

        logger.debug("GetRecord.constructGetRecord: identifier=" + identifier);
        logger.debug("GetRecord.constructGetRecord: metadataPrefix=" + metadataPrefix);

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        String styleSheet = properties.getProperty("OAIHandler.styleSheet");
        if (styleSheet != null) {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            sb.append(styleSheet);
            sb.append("\"?>");
        }
        sb.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        String extraXmlns = properties.getProperty("OAIHandler.extraXmlns");
        if (extraXmlns != null)
            sb.append(" ").append(extraXmlns);
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
        sb.append(" http://ands.org.au/standards/rif-cs/registryObjects");
        sb.append(" http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd\">");
        sb.append("<responseDate>");
        sb.append(createResponseDate(new Date()));
        sb.append("</responseDate>");

        Crosswalks crosswalks = abstractCatalog.getCrosswalks();
        try {
            if (metadataPrefix == null || metadataPrefix.length() == 0
                    || identifier == null || identifier.length() == 0
                    || hasBadArguments(request, validParamNames.iterator(), validParamNames)) {
                throw new BadArgumentException();
            } else if (!crosswalks.containsValue(metadataPrefix)) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            } else {
                String record = abstractCatalog.getRecord(identifier, metadataPrefix);
                if (record != null) {
                    sb.append(getRequestElement(request, validParamNames, baseURL));
                    sb.append("<GetRecord>");
                    sb.append(record);
                    sb.append("</GetRecord>");
                } else {
                    throw new IdDoesNotExistException(identifier);
                }
            }
        } catch (BadArgumentException e) {
            sb.append("<request verb=\"GetRecord\">");
            sb.append(baseURL);
            sb.append("</request>");
            sb.append(e.getMessage());
        } catch (CannotDisseminateFormatException e) {
            sb.append(getRequestElement(request, validParamNames, baseURL));
            sb.append(e.getMessage());
        } catch (IdDoesNotExistException e) {
            sb.append(getRequestElement(request, validParamNames, baseURL));
            sb.append(e.getMessage());
        }
        sb.append("</OAI-PMH>");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}

