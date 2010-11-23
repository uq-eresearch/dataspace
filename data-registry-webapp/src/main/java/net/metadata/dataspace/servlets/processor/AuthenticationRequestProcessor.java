package net.metadata.dataspace.servlets.processor;

import org.apache.abdera.protocol.server.*;

/**
 * Author: alabri
 * Date: 22/11/2010
 * Time: 12:00:36 PM
 */
public class AuthenticationRequestProcessor implements RequestProcessor {

    public ResponseContext process(RequestContext context, WorkspaceManager workspaceManager,
                                   CollectionAdapter collectionAdapter) {
        if (collectionAdapter == null) {
            return ProviderHelper.notfound(context);
        } else {
            return this.processEntry(context, collectionAdapter);
        }
    }

    protected ResponseContext processEntry(RequestContext context, CollectionAdapter adapter) {
        String method = context.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            return adapter.postEntry(context);
        } else if (method.equalsIgnoreCase("POST")) {
            return adapter.postEntry(context);
        } else if (method.equalsIgnoreCase("PUT")) {
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("DELETE")) {
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("HEAD")) {
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            return ProviderHelper.notallowed(context);
        } else {
            return null;
        }
    }
}
