package net.metadata.dataspace.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:07:08 AM
 */
public class DataRegistryApplication {

    public static DataRegistryApplicationConfiguration getApplicationContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext("conf/spring/applicationContext.xml");
        DataRegistryApplicationConfigurationImpl dataRegistryApplicationConfigurationImpl = (DataRegistryApplicationConfigurationImpl) context.getBean("applicationContext");
        return dataRegistryApplicationConfigurationImpl;
    }
}
