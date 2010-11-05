package net.metadata.dataspace.app;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Author: alabri
 * Date: 04/11/2010
 * Time: 4:51:11 PM
 */
public class DataCollectionRegistryTest extends DataCollectionsRegistryTestCase {

    public void testFrontPage() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = (HtmlPage) webClient.getPage("http://localhost:9635");
        waitForDojo();
        assertEquals("UQ Data Collections Registry", page.getTitleText());

        webClient.closeAllWindows();
    }
}
