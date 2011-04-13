package net.metadata.dataspace.atom.util;

import org.apache.abdera.protocol.server.context.SimpleResponseContext;

import java.io.IOException;
import java.io.Writer;

/**
 * Author: alabri
 * Date: 13/04/11
 * Time: 2:39 PM
 */
public class RegistryResponseContext extends SimpleResponseContext {

    public RegistryResponseContext(int status) {
        setStatus(status);
    }

    public RegistryResponseContext(int status, String text) {
        setStatus(status);
        setStatusText(text);
    }

    @Override
    protected void writeEntity(Writer writer) throws IOException {

    }

    @Override
    public boolean hasEntity() {
        return true;
    }
}
