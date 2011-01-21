package net.metadata.dataspace.atom;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Author: alabri
 * Date: 21/01/2011
 * Time: 11:32:25 AM
 */
public class AtomNamespaceContext implements NamespaceContext {

    private static final String ATOM_PREFIX = "atom";
    private static final String APP_PREFIX = "app";
    private static final String ATOM_URI = "http://www.w3.org/2005/Atom";
    private static final String APP_URI = "http://www.w3.org/2007/app";

    public AtomNamespaceContext() {
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(APP_PREFIX)) {
            return APP_URI;
        } else {
            return ATOM_URI;
        }
    }

    @Override
    public String getPrefix(String uri) {
        if (uri.equals(APP_URI)) {
            return APP_PREFIX;
        } else {
            return ATOM_PREFIX;
        }
    }

    @Override
    public Iterator getPrefixes(String s) {
        return null;
    }
}
