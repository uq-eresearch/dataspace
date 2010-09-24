package net.metadata.dataspace.servlets;

import net.metadata.dataspace.atom.adapter.CollectionCollectionAdapter;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:37:18 AM
 */
public class CollectionProviderServlet extends AbderaServlet {

    protected Provider createProvider() {
        CollectionCollectionAdapter ca = new CollectionCollectionAdapter();
        String collectionPath = "collections";
        ca.setHref(collectionPath);

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Collection Directory Workspace");
        wi.addCollection(ca);

        String base = "/collection/";
        DefaultProvider provider = new DefaultProvider(base);
        provider.addWorkspace(wi);

        provider.init(getAbdera(), null);
        return provider;
    }
}

