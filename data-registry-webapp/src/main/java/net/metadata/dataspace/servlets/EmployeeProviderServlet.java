package net.metadata.dataspace.servlets;

import net.metadata.dataspace.atom.adapter.EmployeeCollectionAdapter;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

/**
 * User: alabri
 * Date: 16/09/2010
 * Time: 3:13:12 PM
 */
public final class EmployeeProviderServlet extends AbderaServlet {

    protected Provider createProvider() {
        EmployeeCollectionAdapter ca = new EmployeeCollectionAdapter();
        ca.setHref("employee");

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Employee Directory Workspace");
        wi.addCollection(ca);

        DefaultProvider provider = new DefaultProvider("/");
        provider.addWorkspace(wi);

        provider.init(getAbdera(), null);
        return provider;
    }
}
