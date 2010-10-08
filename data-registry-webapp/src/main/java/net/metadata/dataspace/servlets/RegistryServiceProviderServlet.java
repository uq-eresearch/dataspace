package net.metadata.dataspace.servlets;

import net.metadata.dataspace.atom.adapter.CollectionCollectionAdapter;
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
public class RegistryServiceProviderServlet extends AbderaServlet {

    protected Provider createProvider() {
        //Parties collection and workspace
        PartyCollectionAdapter partyCollectionAdapter = new PartyCollectionAdapter();
        String partiesPath = "parties";
        partyCollectionAdapter.setHref(partiesPath);

        SimpleWorkspaceInfo partyWorkSpace = new SimpleWorkspaceInfo();
        partyWorkSpace.setTitle("Data Collections Registry Workspace");
        partyWorkSpace.addCollection(partyCollectionAdapter);

        //collections collection and workspace
        CollectionCollectionAdapter collectionCollectionAdapter = new CollectionCollectionAdapter();
        String collectionsPath = "collections";
        collectionCollectionAdapter.setHref(collectionsPath);
        partyWorkSpace.addCollection(collectionCollectionAdapter);

        String base = "/";
        DefaultProvider provider = new DefaultProvider(base);

        //Add workspaces
        provider.addWorkspace(partyWorkSpace);

        provider.init(getAbdera(), null);
        return provider;
    }
}
