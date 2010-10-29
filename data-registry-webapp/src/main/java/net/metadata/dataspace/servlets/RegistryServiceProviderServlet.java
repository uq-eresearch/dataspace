package net.metadata.dataspace.servlets;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.atom.adapter.ActivityAdapter;
import net.metadata.dataspace.atom.adapter.CollectionAdapter;
import net.metadata.dataspace.atom.adapter.PartyAdapter;
import net.metadata.dataspace.atom.adapter.ServiceAdapter;
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

        SimpleWorkspaceInfo registryWorkSpace = new SimpleWorkspaceInfo();
        registryWorkSpace.setTitle(DataRegistryApplication.getApplicationContext().getRegistryTitle());

        PartyAdapter partyAdapter = new PartyAdapter();
        partyAdapter.setHref(Constants.PATH_FOR_PARTIES);
        registryWorkSpace.addCollection(partyAdapter);

        CollectionAdapter collectionAdapter = new CollectionAdapter();
        collectionAdapter.setHref(Constants.PATH_FOR_COLLECTIONS);
        registryWorkSpace.addCollection(collectionAdapter);

        ServiceAdapter serviceAdapter = new ServiceAdapter();
        serviceAdapter.setHref(Constants.PATH_FOR_SERVICES);
        registryWorkSpace.addCollection(serviceAdapter);

        ActivityAdapter activityAdapter = new ActivityAdapter();
        activityAdapter.setHref(Constants.PATH_FOR_ACTIVITIES);
        registryWorkSpace.addCollection(activityAdapter);

        String base = "/";
        DefaultProvider registryServiceProvider = new DefaultProvider(base) {
            public ResponseContext process(RequestContext request) {
                Target target = request.getTarget();
                TargetType targetType = target.getType();
                if (targetType.equals(TargetType.get(TargetType.COLLECTION))) {
                    boolean isServiceRequest = target.getParameter("collection").equals("registry.atomsvc");
                    if (isServiceRequest) {
                        TargetType type = TargetType.get(TargetType.SERVICE);
                        RequestProcessor processor = this.requestProcessors.get(type);
                        if (processor == null) {
                            return ProviderHelper.notfound(request);
                        }

                        WorkspaceManager wm = getWorkspaceManager(request);
                        org.apache.abdera.protocol.server.CollectionAdapter adapter = wm.getCollectionAdapter(request);
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
                    } else {
                        return super.process(request);
                    }
                } else {
                    return super.process(request);
                }
            }
        };

        registryServiceProvider.addWorkspace(registryWorkSpace);
        registryServiceProvider.init(getAbdera(), null);
        return registryServiceProvider;
    }

}
