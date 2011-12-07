package net.metadata.dataspace.atom.adapter;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.context.ResponseContextException;

public interface VersionAssembler<R,V> {

	public V assembleAndValidateVersionFromEntry(R record, Entry entry)
			throws ResponseContextException;

}
