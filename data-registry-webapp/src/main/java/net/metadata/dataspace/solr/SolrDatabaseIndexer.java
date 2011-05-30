package net.metadata.dataspace.solr;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Map;

/**
 * Author: alabri
 * Date: 30/05/11
 * Time: 10:46 AM
 */
public class SolrDatabaseIndexer implements Job {

    private Logger logger = Logger.getLogger(getClass());

    private PostMethod postSolrCommand(SolrCommand command) {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(command.toString());
        try {
            logger.info("Sending " + command.getName() + " solr command");
            client.executeMethod(post);
            int statusCode = post.getStatusCode();
            logger.info("Solr server returned " + statusCode);
        } catch (IOException e) {
            logger.fatal("Could not post solr command (" + command.getName() + ")");
        }
        return post;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Map dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        logger.debug("Executing " + dataMap.size() + " solr commands");
        for (Object key : dataMap.keySet()) {
            SolrCommand task = (SolrCommand) dataMap.get(key);
            postSolrCommand(task);
        }
        logger.debug("Executed " + dataMap.size() + " solr commands");
    }
}
