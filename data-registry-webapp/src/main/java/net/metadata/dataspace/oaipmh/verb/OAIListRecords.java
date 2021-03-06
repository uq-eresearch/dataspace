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
import java.util.*;

/**
 * Represents an OAI ListRecords Verb response. This class is used on both
 * the client-side and on the server-side to represent a ListRecords response
 * <p/>
 * Author: alabri
 * Date: 10/02/2011
 * Time: 3:21:10 PM
 */

public class OAIListRecords extends ServerVerb {

    private static Logger logger = Logger.getLogger(OAIListRecords.class);
    private static ArrayList<String> validParamNames1 = new ArrayList<String>();

    static {
        validParamNames1.add("verb");
        validParamNames1.add("from");
        validParamNames1.add("until");
        validParamNames1.add("set");
        validParamNames1.add("metadataPrefix");
    }

    private static ArrayList<String> validParamNames2 = new ArrayList<String>();

    static {
        validParamNames2.add("verb");
        validParamNames2.add("resumptionToken");
    }

    private static ArrayList<String> requiredParamNames1 = new ArrayList<String>();

    static {
        requiredParamNames1.add("verb");
        requiredParamNames1.add("metadataPrefix");
    }

    private static ArrayList<String> requiredParamNames2 = new ArrayList<String>();

    static {
        requiredParamNames2.add("verb");
        requiredParamNames2.add("resumptionToken");
    }

    /**
     * Server-side method to construct an xml response to a ListRecords verb.
     */
    public static String construct(HashMap<?, ?> context,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Transformer serverTransformer)
            throws OAIInternalServerError, TransformerException {
        logger.debug("ListRecords.construct: entered");

        Properties properties = (Properties) context.get("OAIHandler.properties");
        AbstractCatalog abstractCatalog = (AbstractCatalog) context.get("OAIHandler.catalog");
        boolean xmlEncodeSetSpec = "true".equalsIgnoreCase(properties.getProperty("OAIHandler.xmlEncodeSetSpec"));
        boolean urlEncodeSetSpec = !"false".equalsIgnoreCase(properties.getProperty("OAIHandler.urlEncodeSetSpec"));
        String baseURL = properties.getProperty("OAIHandler.baseURL");
        if (baseURL == null) {
            try {
                baseURL = request.getRequestURL().toString();
            } catch (java.lang.NoSuchMethodError f) {
                baseURL = HttpUtils.getRequestURL(request).toString();
            }
        }
        StringBuffer sb = new StringBuffer();
        String oldResumptionToken = request.getParameter("resumptionToken");
        String metadataPrefix = request.getParameter("metadataPrefix");

        if (metadataPrefix != null && metadataPrefix.length() == 0)
            metadataPrefix = null;

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
        if (!abstractCatalog.isHarvestable()) {
            sb.append("<request verb=\"ListRecords\">");
            sb.append(baseURL);
            sb.append("</request>");
            sb.append("<error code=\"badArgument\">Database is unavailable for harvesting</error>");
        } else {
            Map<?, ?> listRecordsMap = null;
            ArrayList<String> validParamNames = null;
            ArrayList<String> requiredParamNames = null;
            if (oldResumptionToken == null) {
                validParamNames = validParamNames1;
                requiredParamNames = requiredParamNames1;
                String from = request.getParameter("from");
                String until = request.getParameter("until");
                try {
                    if (from != null && from.length() > 0 && from.length() < 10) {
                        throw new BadArgumentException();
                    }
                    if (until != null && until.length() > 0 && until.length() < 10) {
                        throw new BadArgumentException();
                    }
                    if (from != null && until != null && from.length() != until.length()) {
                        throw new BadArgumentException();
                    }
                    if (from == null || from.length() == 0) {
                        from = "0001-01-01";
                    }
                    if (until == null || until.length() == 0) {
                        until = "9999-12-31";
                    }
                    from = abstractCatalog.toFinestFrom(from);
                    until = abstractCatalog.toFinestUntil(until);
                    if (from.compareTo(until) > 0)
                        throw new BadArgumentException();
                    String set = request.getParameter("set");
                    if (set != null) {
                        if (set.length() == 0) set = null;
                        else if (urlEncodeSetSpec) set = set.replace(' ', '+');
                    }
                    Crosswalks crosswalks = abstractCatalog.getCrosswalks();
                    if (metadataPrefix == null) {
                        throw new BadArgumentException();
                    }
                    if (!crosswalks.containsValue(metadataPrefix)) {
                        throw new CannotDisseminateFormatException(metadataPrefix);
                    } else {
                        listRecordsMap = abstractCatalog.listRecords(from, until, set,
                                metadataPrefix);
                    }
                } catch (NoItemsMatchException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                } catch (BadArgumentException e) {
                    sb.append("<request verb=\"ListRecords\">");
                    sb.append(baseURL);
                    sb.append("</request>");
                    sb.append(e.getMessage());
                } catch (CannotDisseminateFormatException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                } catch (NoSetHierarchyException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                }
            } else {
                validParamNames = validParamNames2;
                requiredParamNames = requiredParamNames2;
                if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(new BadArgumentException().getMessage());
                } else {
                    try {
                        listRecordsMap = abstractCatalog.listRecords(oldResumptionToken);
                    } catch (BadResumptionTokenException e) {
                        sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                        sb.append(e.getMessage());
                    }
                }
            }
            if (listRecordsMap != null) {
                sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                if (hasBadArguments(request, requiredParamNames.iterator(),
                        validParamNames)) {
                    sb.append(new BadArgumentException().getMessage());
                } else {
                    sb.append("<ListRecords>\n");
                    Iterator<?> records = (Iterator<?>) listRecordsMap.get("records");
                    while (records.hasNext()) {
                        sb.append((String) records.next());
                        sb.append("\n");
                    }
                    Map<?, ?> newResumptionMap = (Map<?, ?>) listRecordsMap.get("resumptionMap");
                    if (newResumptionMap != null) {
                        String newResumptionToken = (String) newResumptionMap.get("resumptionToken");
                        String expirationDate = (String) newResumptionMap.get("expirationDate");
                        String completeListSize = (String) newResumptionMap.get("completeListSize");
                        String cursor = (String) newResumptionMap.get("cursor");
                        sb.append("<resumptionToken");
                        if (expirationDate != null) {
                            sb.append(" expirationDate=\"");
                            sb.append(expirationDate);
                            sb.append("\"");
                        }
                        if (completeListSize != null) {
                            sb.append(" completeListSize=\"");
                            sb.append(completeListSize);
                            sb.append("\"");
                        }
                        if (cursor != null) {
                            sb.append(" cursor=\"");
                            sb.append(cursor);
                            sb.append("\"");
                        }
                        sb.append(">");
                        sb.append(newResumptionToken);
                        sb.append("</resumptionToken>");
                    } else if (oldResumptionToken != null) {
                        sb.append("<resumptionToken />");
                    }
                    sb.append("</ListRecords>");
                }
            }
        }
        sb.append("</OAI-PMH>");

        logger.debug("ListRecords.constructListRecords: returning: " + sb.toString());

        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
