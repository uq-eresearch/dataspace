package net.metadata.dataspace.app;

import net.sourceforge.jwebunit.junit.WebTestCase;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 4:47:17 PM
 */
public abstract class DataCollectionsRegistryTestCase extends WebTestCase {

//    protected void setUp() throws Exception {
//        String baseUrl = System.getProperty("data.registry.uri.prefix", "http://localhost:9635");
//        setBaseUrl(baseUrl);
//        gotoPage(baseUrl + "/");
//    }

    /**
     * Waits for a bit to let Dojo do its thing.
     * <p/>
     * It seems that sometimes we get race conditions between our calls and Dojo's changing
     * of widgets. We should find a way to synchronize properly, but for now we just wait.
     * <p/>
     * See also http://htmlunit.sourceforge.net/faq.html#AJAXDoesNotWork -- that one refers
     * to AJAX and we can't actually access the WebClient instance (despite using it underneath),
     * but at least the general idea is the same. We would just need to figure out how to
     * reliably determine that Dojo has finished setting up the page.
     */
    protected void waitForDojo() {
        try {
            Thread.sleep(2000); // at 1sec we still had sporadic failures
        } catch (InterruptedException e) {
            // should not happen
        }
    }
}
