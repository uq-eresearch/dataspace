package net.metadata.dataspace.solr;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        Properties solrProperties = loadSolrProperties();
        JobDetail job = new JobDetail();
        job.setName("solrIndexing");
        job.setJobClass(SolrDatabaseIndexer.class);
        Map dataMap = job.getJobDataMap();
        SolrCommand command = getFullImportCommand(solrProperties);
        dataMap.put("fullImport", command);

        String seconds = getProperty(solrProperties, "solr.indexing.interval.seconds", "0");
        String minutes = getProperty(solrProperties, "solr.indexing.interval.minutes", "45");
        String hours = getProperty(solrProperties, "solr.indexing.interval.hours", "*");

        //configure the scheduler time
        CronTrigger trigger = new CronTrigger();
        trigger.setName("runMeJobTesting");
        try {
            String cronExpression = seconds + minutes + " " + hours + " * * ?";
            logger.info("Indexing cron expression ======> " + cronExpression);
            trigger.setCronExpression(cronExpression);
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

    private SolrCommand getFullImportCommand(Properties properties) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("command", "full-import");
        String server = getProperty(properties, "solr.server", "http://localhost");
        int port = Integer.parseInt(getProperty(properties, "solr.port", "8080"));
        String webapp = getProperty(properties, "solr.webapp", "solr");
        return new SolrCommand(server, port, webapp, "dataimport", params);
    }

    private Properties loadSolrProperties() {
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = SolrIndexingListener.class.getResourceAsStream("/conf/solr/solr.properties");
            if (resourceAsStream == null) {
                logger.fatal("Solr configuration file not found, please ensure there is a 'conf/solr/solr.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            logger.fatal("Failed to load solr configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
        return properties;
    }

    private static String getProperty(Properties properties, String propertyName, String defaultValue) {
        String result = properties.getProperty(propertyName);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
}
