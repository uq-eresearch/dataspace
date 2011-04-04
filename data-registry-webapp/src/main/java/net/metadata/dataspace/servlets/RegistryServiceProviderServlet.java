package net.metadata.dataspace.servlets;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.adapter.ActivityAdapter;
import net.metadata.dataspace.atom.adapter.AgentAdapter;
import net.metadata.dataspace.atom.adapter.CollectionAdapter;
import net.metadata.dataspace.atom.adapter.ServiceAdapter;
import net.metadata.dataspace.servlets.processor.VersionRequestProcessor;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.templates.Route;
import org.apache.abdera.protocol.server.*;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 10:59:54 AM
 */
public class RegistryServiceProviderServlet extends AbderaServlet {

    private Logger logger = Logger.getLogger(getClass());

    protected Provider createProvider() {

        SimpleWorkspaceInfo registryWorkSpace = new SimpleWorkspaceInfo();
        registryWorkSpace.setTitle(RegistryApplication.getApplicationContext().getRegistryTitle());

        AgentAdapter agentAdapter = new AgentAdapter();
        agentAdapter.setHref(Constants.PATH_FOR_AGENTS);
        registryWorkSpace.addCollection(agentAdapter);

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
            RouteManager targetResolver = (RouteManager) this.getTargetResolver();

            @Override
            public void init(Abdera abdera, Map<String, String> properties) {
                super.init(abdera, properties);
                this.requestProcessors.put(TargetType.get(Constants.TARGET_TYPE_VERSION, true), new VersionRequestProcessor());
//                this.requestProcessors.put(TargetType.get(Constants.TARGET_TYPE_WORKING_COPY, true), new VersionRequestProcessor());
                this.targetResolver.addRoute(new Route(Constants.TARGET_TYPE_VERSION, "/:collection/:entry/:version"), TargetType.get(Constants.TARGET_TYPE_VERSION));
//                this.targetResolver.addRoute(new Route(Constants.TARGET_TYPE_WORKING_COPY, "/:collection/:entry/:working-copy"), TargetType.get(Constants.TARGET_TYPE_WORKING_COPY));
            }

            public ResponseContext process(RequestContext request) {
                Target target = request.getTarget();
                TargetType targetType = target.getType();
                if (targetType.equals(TargetType.get(TargetType.COLLECTION))) {
                    //TODO looks like a hack, find a better way to do this later.
                    boolean isServiceRequest = target.getParameter("collection").equals("registry.atomsvc");
                    boolean isLoginRequest = target.getParameter("collection").equals("login");
                    boolean isLogoutRequest = target.getParameter("collection").equals("logout");
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
                    } else if (isLoginRequest) {
                        return login(request);
                    } else if (isLogoutRequest) {
                        return logout(request);
                    } else {
                        return super.process(request);
                    }
//                }else if (targetType.equals(TargetType.get(Constants.TARGET_LOGIN))) {
//                    return login(request);
//                }else if (targetType.equals(TargetType.get(Constants.TARGET_LOGOUT))) {
//                    return logout(request);
                } else {
                    return super.process(request);
                }
            }

            protected ResponseContext login(RequestContext request) {
                return RegistryApplication.getApplicationContext().getAuthenticationManager().login(request);
            }

            protected ResponseContext logout(RequestContext request) {
                return RegistryApplication.getApplicationContext().getAuthenticationManager().logout(request);
            }
        };
        registryServiceProvider.addWorkspace(registryWorkSpace);
        registryServiceProvider.init(getAbdera(), null);
        return registryServiceProvider;
    }

}
