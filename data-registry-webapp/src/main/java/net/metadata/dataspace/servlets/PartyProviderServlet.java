package net.metadata.dataspace.servlets;

import net.metadata.dataspace.atom.adapter.PartyCollectionAdapter;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 10:59:54 AM
 */
public class PartyProviderServlet extends AbderaServlet {

    protected Provider createProvider() {
        PartyCollectionAdapter ca = new PartyCollectionAdapter();
        String collectionPath = "partycollection";
        ca.setHref(collectionPath);

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Party Directory Workspace");
        wi.addCollection(ca);

        String base = "/party/";
        DefaultProvider provider = new DefaultProvider(base);
        provider.addWorkspace(wi);

        provider.init(getAbdera(), null);
        return provider;
    }
}
