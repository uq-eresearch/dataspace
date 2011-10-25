package net.metadata.dataspace.solr;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.xml.sax.SAXException;

public class SolrServerManager {

    private Logger logger = Logger.getLogger(getClass());

    private SolrServer server;

    public SolrServerManager() throws IOException, ParserConfigurationException, SAXException {
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        server = new EmbeddedSolrServer(coreContainer, "");
    }

    public void reindex() {
        if (getServer() == null)
            return;
        logger.info("Clearing Solr index");
        try {
            getServer().deleteByQuery("*:*");
        } catch (SolrServerException e1) {
            logger.error("Solr exception while clearing index:"+e1.getMessage());
            e1.printStackTrace();
            return;
        } catch (IOException e1) {
            logger.error("IO exception while clearing index:"+e1.getMessage());
            e1.printStackTrace();
            return;
        }
        logger.info("Reindexing Solr with DataImport");
        ModifiableSolrParams reindexParams = new ModifiableSolrParams();
        reindexParams.add("command","full-import");
        DirectXmlRequest dataimportRequest =
                new DirectXmlRequest("/dataimport", "");
        dataimportRequest.setParams(reindexParams);
        try {
            getServer().request(dataimportRequest);
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public SolrServer getServer() {
        return server;
    }
}
