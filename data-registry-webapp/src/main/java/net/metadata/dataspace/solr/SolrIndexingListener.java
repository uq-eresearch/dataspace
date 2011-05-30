package net.metadata.dataspace.solr;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: alabri
 * Date: 30/05/11
 * Time: 11:27 AM
 */
public class SolrIndexingListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initialising solr indexing scheduler ...");
        JobDetail job = new JobDetail();
        job.setName("solrIndexing");
        job.setJobClass(SolrDatabaseIndexer.class);
        Map dataMap = job.getJobDataMap();
        SolrCommand command = getFullImportCommand();
        dataMap.put("fullImport", command);

        //configure the scheduler time
        CronTrigger trigger = new CronTrigger();
        trigger.setName("runMeJobTesting");
        try {
            trigger.setCronExpression("5 * * * * ?");
        } catch (ParseException e) {
            logger.warn("Could not parse cron expression");
        }

        //schedule it
        Scheduler scheduler = null;
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            sce.getServletContext().setAttribute("scheduler", scheduler);
            logger.info("Successfully initialised solr indexing scheduler");
        } catch (SchedulerException e) {
            logger.warn("Could not start solr indexing cron job");
        }
    }

    private SolrCommand getFullImportCommand() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("command", "full-import");
        return new SolrCommand("http://localhost", 8080, "solr", "dataimport", params);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Scheduler scheduler = (Scheduler) sce.getServletContext().getAttribute("scheduler");
        try {
            scheduler.shutdown(false);
            logger.info("Successfully shutdown solr indexing scheduler");
        } catch (SchedulerException e) {
            logger.warn("Failed to shutdown solr indexing scheduler");
        }
    }
}
