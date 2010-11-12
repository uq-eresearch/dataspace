package net.metadata.dataspace.servlets.processor;

import org.apache.abdera.protocol.server.*;

/**
 * Author: alabri
 * Date: 12/11/2010
 * Time: 2:30:34 PM
 */
public class VersionRequestProcessor implements RequestProcessor {

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
            return adapter.getEntry(context);
        } else if (method.equalsIgnoreCase("PUT")) {
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("DELETE")) {
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("HEAD")) {
            //TODO maybe implement this later
            return ProviderHelper.notallowed(context);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            return ProviderHelper.notallowed(context);
        } else {
            return null;
        }
    }
}

