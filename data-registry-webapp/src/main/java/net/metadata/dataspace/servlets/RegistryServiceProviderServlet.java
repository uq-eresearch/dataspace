package net.metadata.dataspace.servlets;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryConfiguration;
import net.metadata.dataspace.atom.adapter.ActivityAdapter;
import net.metadata.dataspace.atom.adapter.AgentAdapter;
import net.metadata.dataspace.atom.adapter.CollectionAdapter;
import net.metadata.dataspace.atom.adapter.ServiceAdapter;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.servlets.processor.VersionRequestProcessor;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.templates.Route;
import org.apache.abdera.protocol.server.*;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.RouteManager;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 10:59:54 AM
 */
public class RegistryServiceProviderServlet extends AbderaServlet {

    /**
	 *
	 */
	private static final long serialVersionUID = -6861319035092045064L;
	private Logger logger = Logger.getLogger(getClass());


	static class FileExtWorkspaceManager extends DefaultWorkspaceManager {

		@Override
		public org.apache.abdera.protocol.server.CollectionAdapter getCollectionAdapter(RequestContext request) {
			org.apache.abdera.protocol.server.CollectionAdapter ca = super.getCollectionAdapter(request);
	        if (ca != null) {
	            return ca;
	        }
	        // Otherwise, check again using "." as a delimiter
	        String path = request.getContextPath() + request.getTargetPath();

	        for (WorkspaceInfo wi : workspaces) {
	            for (CollectionInfo ci : wi.getCollections(request)) {
	                String href = ci.getHref(request);
	                if (path.equals(href) || (href != null && path.startsWith(href) && ".".equals(path
	                    .substring(href.length(), href.length() + 1)))) {
	                    return (org.apache.abdera.protocol.server.CollectionAdapter)ci;
	                }
	            }
	        }

	        return null;
	    }
	}

	protected Provider createProvider() {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        SimpleWorkspaceInfo registryWorkSpace = new SimpleWorkspaceInfo();
        registryWorkSpace.setTitle(((RegistryConfiguration)context.getBean("applicationContext")).getRegistryTitle());

        registryWorkSpace.addCollection((CollectionInfo) context.getBean("agentAdapter"));
        registryWorkSpace.addCollection((CollectionInfo) context.getBean("collectionAdapter"));
        registryWorkSpace.addCollection((CollectionInfo) context.getBean("serviceAdapter"));
        registryWorkSpace.addCollection((CollectionInfo) context.getBean("activityAdapter"));

        String base = "/";
        DefaultProvider registryServiceProvider = new DefaultProvider(base) {
            RouteManager targetResolver = (RouteManager) this.getTargetResolver();

            @Override
            public void init(Abdera abdera, Map<String, String> properties) {
                super.init(abdera, properties);
                this.requestProcessors.put(TargetType.get(Constants.TARGET_TYPE_VERSION, true), new VersionRequestProcessor());
//                this.requestProcessors.put(TargetType.get(Constants.TARGET_TYPE_WORKING_COPY, true), new VersionRequestProcessor());
//                this.targetResolver.addRoute(new Route(Constants.TARGET_TYPE_VERSION, "/:collection/:entry/:version"), TargetType.get(Constants.TARGET_TYPE_VERSION));
//                this.targetResolver.addRoute(new Route(Constants.TARGET_TYPE_WORKING_COPY, "/:collection/:entry/:working-copy"), TargetType.get(Constants.TARGET_TYPE_WORKING_COPY));

                super.setTargetResolver(
	                new RegexTargetResolver()
			        	.setPattern("/(registry.atomsvc)", TargetType.TYPE_SERVICE, "service")
				        .setPattern("/([^/#?\\.]+)(\\.\\w+)?/?(\\?[^#]*)?", TargetType.TYPE_COLLECTION, "collection")
				        .setPattern("/([^/#?]+)/([^/#?\\.]+)(\\.\\w+)?/?(\\?[^#]*)?", TargetType.TYPE_ENTRY, "collection","entry")
				        .setPattern("/([^/#?]+)/([^/#?]+)/([^/#?\\.]+)(\\.\\w+)?/?(\\?[^#]*)?", TargetType.get(Constants.TARGET_TYPE_VERSION), "collection","entry","version")
	                );
            }

            public ResponseContext process(RequestContext request) {
                Target target = request.getTarget();
                TargetType targetType = target.getType();
                if (targetType.equals(TargetType.TYPE_COLLECTION)) {
                    //TODO looks like a hack, find a better way to do this later.
                    boolean isLoginRequest = target.getParameter("collection").equals("login");
                    boolean isLogoutRequest = target.getParameter("collection").equals("logout");
                    if (isLoginRequest) {
                        return login(request);
                    } else if (isLogoutRequest) {
                        return logout(request);
                    } else {
                        return super.process(request);
                    }
                } else {
                    return super.process(request);
                }
            }

            protected ResponseContext login(RequestContext request) {
                return getAuthenticationManager().login(request);
            }

            protected ResponseContext logout(RequestContext request) {
                return getAuthenticationManager().logout(request);
            }
        };
        registryServiceProvider.setWorkspaceManager(new FileExtWorkspaceManager());
        registryServiceProvider.addWorkspace(registryWorkSpace);
        registryServiceProvider.init(getAbdera(), null);
        return registryServiceProvider;
    }

	private AuthenticationManager getAuthenticationManager() {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return (AuthenticationManager) context.getBean("authenticationManager");
	}

}
