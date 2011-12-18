package net.metadata.dataspace.atom.writer;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.model.record.User;
import org.apache.abdera.model.Base;
import org.apache.abdera.parser.stax.util.PrettyWriter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.util.AbstractNamedWriter;
import org.apache.abdera.util.AbstractWriterOptions;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.WriterOptions;
import org.apache.log4j.Logger;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * Author: alabri
 * Date: 26/11/2010
 * Time: 1:56:34 PM
 */
public class XSLTTransformerWriter extends AbstractNamedWriter implements NamedWriter {

    private Logger logger = Logger.getLogger(getClass());
    private static final String[] FORMATS = {Constants.MIME_TYPE_RDF, Constants.MIME_TYPE_RIFCS};
    private String XSL = "";
    private RequestContext contextRequest;
    private String version = RegistryApplication.getApplicationContext().getVersion();
    private String title = RegistryApplication.getApplicationContext().getRegistryTitle();

    public XSLTTransformerWriter(String xslFilePath) {
        super("XSLTTransformer", FORMATS);
        this.XSL = xslFilePath;
    }

    public XSLTTransformerWriter(String xslFilePath, RequestContext request) {
        super("XSLTTransformer", FORMATS);
        this.XSL = xslFilePath;
        this.contextRequest = request;
    }

    @Override
    protected WriterOptions initDefaultWriterOptions() {
        return new AbstractWriterOptions() {
        };
    }

    @Override
    public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException {
        out = getCompressedOutputStream(out, options);
        String charset = options.getCharset() != null ? options.getCharset() : "UTF-8";
        writeTo(base, new OutputStreamWriter(out, charset), options);
        finishCompressedOutputStream(out, options);
        if (options.getAutoClose())
            out.close();
    }

    @Override
    public Object write(Base base, WriterOptions options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(base, out, options);
        return out.toString();
    }

    @Override
    public void writeTo(Base base, Writer out, WriterOptions options) throws IOException {
        URL xslResource = XSLTTransformerWriter.class.getResource(this.XSL);
        String xslPath = xslResource.getPath();
        File xslFile = new File(xslPath);
        //TODO need to find a better way to pipe in stream between ouputstream and inputstream
        File xmlFile = File.createTempFile("dataspace-xsl", null);
        PrettyWriter writer = new PrettyWriter();
        OutputStream fileOS = new FileOutputStream(xmlFile);
        writer.writeTo(base, fileOS);
        try {
            process(xmlFile, xslFile, out);
        } catch (TransformerException e) {
            logger.warn("Error while transforming atom representation using:\n" + xslPath, e);
        } finally {
            xmlFile.delete();
        }

    }

    private void process(File xmlFile, File xslFile, Writer output) throws TransformerException {
        process(new StreamSource(xmlFile), new StreamSource(xslFile), new StreamResult(output));
    }

    private void process(Source xml, Source xsl, Result result) throws TransformerException {
        try {
        	// Explicitly use Saxon (so that we get XSLT 2.0)
            Templates template = net.sf.saxon.TransformerFactoryImpl.newInstance().newTemplates(xsl);
            Transformer transformer = template.newTransformer();
            if (this.contextRequest != null) {
                AuthenticationManager authenticationManager = RegistryApplication.getApplicationContext().getAuthenticationManager();
                User currentUser = authenticationManager.getCurrentUser(this.contextRequest);
                if (currentUser != null) {
                    transformer.setParameter(Constants.SESSION_ATTRIBUTE_CURRENT_USER, currentUser.getDisplayName());
                    transformer.setParameter(Constants.SESSION_ATTRIBUTE_CURRENT_EMAIL, currentUser.getEmail());
                }
            }
            transformer.setParameter("applicationName", title);
            transformer.setParameter("applicationVersion", version);
            transformer.transform(xml, result);
        } catch (TransformerConfigurationException tce) {
            throw new TransformerException(tce.getMessageAndLocation());
        } catch (TransformerException te) {
            throw new TransformerException(te.getMessageAndLocation());
        }
    }

}
