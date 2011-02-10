package net.metadata.dataspace.servlets;

import ORG.oclc.oai.server.OAIHandler;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import net.metadata.dataspace.app.RegistryApplication;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 03/02/2011
 * Time: 11:17:59 AM
 */
public class OAIPMHServlet extends OAIHandler {
    private static final long serialVersionUID = 1L;
    private static final String VERSION = "1.5.57";
    private Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings({"unchecked", "rawtypes"})
    public HashMap getAttributes(Properties properties) {
        try {
            properties = RegistryApplication.getApplicationContext().getOaiProperties();
            HashMap attributes = new HashMap();
            Enumeration attrNames = getServletContext().getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = (String) attrNames.nextElement();
                attributes.put(attrName, getServletContext().getAttribute(attrName));
            }
            attributes.put("OAIHandler.properties", properties);
            String missingVerbClassName = properties.getProperty("OAIHandler.missingVerbClassName", "ORG.oclc.oai.server.verb.BadVerb");
            Class missingVerbClass = Class.forName(missingVerbClassName);
            attributes.put("OAIHandler.missingVerbClass", missingVerbClass);
            if (!"true".equals(properties.getProperty("OAIHandler.serviceUnavailable"))) {
                attributes.put("OAIHandler.version", VERSION);
                AbstractCatalog abstractCatalog = RegistryApplication.getApplicationContext().getOaiCatalog();
                attributes.put("OAIHandler.catalog", abstractCatalog);
            }
            String appBase = properties.getProperty("OAIHandler.appBase");
            if (appBase == null)
                appBase = "webapps";

            return attributes;
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Get Request is " + request.getRequestURL() + "?" + request.getQueryString());
        super.doGet(request, response);
        logger.debug("End Get Request");
    }

    @Override
    public HashMap getAttributes(String pathInfo) {
        logger.debug("Path info: " + pathInfo);
        return super.getAttributes(pathInfo);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Post Request is " + request.getRequestURL());
        super.doPost(request, response);
    }
}
