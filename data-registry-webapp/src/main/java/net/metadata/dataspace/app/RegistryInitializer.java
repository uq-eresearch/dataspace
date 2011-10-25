package net.metadata.dataspace.app;

import net.metadata.dataspace.util.ANZSRCLoader;
import org.apache.log4j.Logger;

/**
 * Author: alabri
 * Date: 1/06/11
 * Time: 12:57 PM
 * A listener for custom initializations
 */
public class RegistryInitializer {
    private Logger logger = Logger.getLogger(getClass());

    public RegistryInitializer(String loadAnzsrcCodes) {
        if (Boolean.valueOf(loadAnzsrcCodes)) {
            injectANZSRCCodes();
        }
    }

    private void injectANZSRCCodes() {
        logger.info("Injecting ANZSRC codes...");
        boolean result = ANZSRCLoader.loadANZSRCCodes();
        if (result) {
            logger.info("Injected ANZSRC codes.............OK");
        } else {
            logger.info("Injection of ANZSRC codes was not successful");
        }
    }

}
