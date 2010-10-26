package net.metadata.dataspace.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: alabri
 * Date: 20/09/2010
 * Time: 11:07:08 AM
 */
public class DataRegistryApplication {
    private static ApplicationContext context = new ClassPathXmlApplicationContext("conf/spring/applicationContext.xml");

    public static DataRegistryApplicationConfiguration getApplicationContext() {
        DataRegistryApplicationConfigurationImpl dataRegistryApplicationConfigurationImpl = (DataRegistryApplicationConfigurationImpl) context.getBean("applicationContext");
        return dataRegistryApplicationConfigurationImpl;
    }
}
