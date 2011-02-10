package net.metadata.dataspace.servlets;

import ORG.oclc.oai.server.OAIHandler;
import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import ORG.oclc.oai.server.verb.ServerVerb;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.oaipmh.verb.OAIListRecords;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.Date;
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
        HashMap attributes = getAttributes(request.getPathInfo());
        if (!filterRequest(request, response)) {
            return;
        }
        logger.debug("attributes=" + attributes);
        Properties properties =
                (Properties) attributes.get("OAIHandler.properties");
        boolean monitor = false;
        if (properties.getProperty("OAIHandler.monitor") != null) {
            monitor = true;
        }
        boolean serviceUnavailable = isServiceUnavailable(properties);
        String extensionPath = properties.getProperty("OAIHandler.extensionPath", "/extension");

        HashMap serverVerbs = ServerVerb.getVerbs(properties);
        serverVerbs.put("ListRecords", OAIListRecords.class);
        HashMap extensionVerbs = ServerVerb.getExtensionVerbs(properties);

        Transformer transformer =
                (Transformer) attributes.get("OAIHandler.transformer");

        boolean forceRender = false;
        if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
            forceRender = true;
        }

        request.setCharacterEncoding("UTF-8");

        Date then = null;
        if (monitor) then = new Date();
        if (serviceUnavailable) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Sorry. This server is down for maintenance");
        } else {
            try {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent == null) {
                    userAgent = "";
                } else {
                    userAgent = userAgent.toLowerCase();
                }
                Transformer serverTransformer = null;
                if (transformer != null) {

                    // return HTML if the client is an old browser
                    if (forceRender
                            || userAgent.indexOf("opera") != -1
                            || (userAgent.startsWith("mozilla")
                            && userAgent.indexOf("msie 6") == -1
                            /* && userAgent.indexOf("netscape/7") == -1 */)) {
                        serverTransformer = transformer;
                    }
                }
                String result = getResult(attributes, request, response, serverTransformer, serverVerbs, extensionVerbs, extensionPath);
                logger.debug("result=" + result);


                Writer out = getWriter(request, response);
                out.write(result);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.debug("SC_NOT_FOUND: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (TransformerException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (OAIInternalServerError e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (SocketException e) {
                logger.debug(e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        if (monitor) {
            StringBuffer reqUri = new StringBuffer(request.getRequestURI().toString());
            String queryString = request.getQueryString();   // d=789
            if (queryString != null) {
                reqUri.append("?").append(queryString);
            }
            Runtime rt = Runtime.getRuntime();
            logger.debug(rt.freeMemory() + "/" + rt.totalMemory() + " "
                    + ((new Date()).getTime() - then.getTime()) + "ms: "
                    + reqUri.toString());
        }
        logger.debug("End Get Request");
    }

    public static String getResult(HashMap attributes,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Transformer serverTransformer,
                                   HashMap serverVerbs,
                                   HashMap extensionVerbs,
                                   String extensionPath)
            throws Throwable {
        try {
            boolean isExtensionVerb = extensionPath.equals(request.getPathInfo());
            String verb = request.getParameter("verb");

            String result;
            Class verbClass = null;
            if (isExtensionVerb) {
                verbClass = (Class) extensionVerbs.get(verb);
            } else {
                verbClass = (Class) serverVerbs.get(verb);
            }
            if (verbClass == null) {
                verbClass = (Class) attributes.get("OAIHandler.missingVerbClass");
            }
            Method construct = verbClass.getMethod("construct",
                    new Class[]{HashMap.class,
                            HttpServletRequest.class,
                            HttpServletResponse.class,
                            Transformer.class});
            try {
                result = (String) construct.invoke(null,
                        new Object[]{attributes,
                                request,
                                response,
                                serverTransformer});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            return result;
        } catch (NoSuchMethodException e) {
            throw new OAIInternalServerError(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new OAIInternalServerError(e.getMessage());
        }
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
