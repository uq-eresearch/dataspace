package net.metadata.dataspace.servlets;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.adapter.CollectionCollectionAdapter;
import net.metadata.dataspace.atom.adapter.PartyCollectionAdapter;
import org.apache.abdera.protocol.server.*;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.log4j.Logger;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 10:59:54 AM
 */
public class RegistryServiceProviderServlet extends AbderaServlet {

    private Logger logger = Logger.getLogger(getClass());

    protected Provider createProvider() {
        //Parties collection and workspace
        PartyCollectionAdapter partyCollectionAdapter = new PartyCollectionAdapter();
        String partiesPath = "parties";
        partyCollectionAdapter.setHref(partiesPath);

        SimpleWorkspaceInfo partyWorkSpace = new SimpleWorkspaceInfo();
        partyWorkSpace.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle());
        partyWorkSpace.addCollection(partyCollectionAdapter);

        //collections collection and workspace
        CollectionCollectionAdapter collectionCollectionAdapter = new CollectionCollectionAdapter();
        String collectionsPath = "collections";
        collectionCollectionAdapter.setHref(collectionsPath);
        partyWorkSpace.addCollection(collectionCollectionAdapter);

        String base = "/";
        DefaultProvider provider = new DefaultProvider(base) {
            public ResponseContext process(RequestContext request) {
                Target target = request.getTarget();

                boolean isServiceRequest = target.getParameter("collection").equals("registry.atomsvc");
                if (isServiceRequest) {
                    TargetType type = TargetType.get(TargetType.SERVICE);
                    RequestProcessor processor = this.requestProcessors.get(type);
                    if (processor == null) {
                        return ProviderHelper.notfound(request);
                    }

                    WorkspaceManager wm = getWorkspaceManager(request);
                    CollectionAdapter adapter = wm.getCollectionAdapter(request);
                    Transactional transaction = adapter instanceof Transactional ? (Transactional) adapter : null;
                    ResponseContext response = null;
                    try {
                        transactionStart(transaction, request);
                        response = processor.process(request, wm, adapter);
                        response = response != null ? response : processExtensionRequest(request, adapter);
                    } catch (Throwable e) {
                        if (e instanceof ResponseContextException) {
                            ResponseContextException rce = (ResponseContextException) e;
                            if (rce.getStatusCode() >= 400 && rce.getStatusCode() < 500) {
                                // don't report routine 4xx HTTP errors
                                logger.info(e);
                            } else {
                                logger.error(e);
                            }
                        } else {
                            logger.error(e);
                        }
                        transactionCompensate(transaction, request, e);
                        response = createErrorResponse(request, e);
                        return response;
                    } finally {
                        transactionEnd(transaction, request, response);
                    }
                    return response != null ? response : ProviderHelper.badrequest(request);
                }
                return super.process(request);
            }
        };

        //Add workspaces
        provider.addWorkspace(partyWorkSpace);
        provider.init(getAbdera(), null);
        return provider;
    }

}
