package net.metadata.dataspace.atom.adapter;

import java.util.List;

import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;

import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractRecordAdapter<R extends Record<?>> extends
		AbstractEntityCollectionAdapter<R> {

	protected Logger logger = Logger.getLogger(getClass());

	private HttpMethodHelper httpMethodHelper;

	public AbstractRecordAdapter() {
		super();
	}

	public HttpMethodHelper getHttpMethodHelper() {
		return httpMethodHelper;
	}

	@Required
	public void setHttpMethodHelper(HttpMethodHelper httpMethodHelper) {
		this.httpMethodHelper = httpMethodHelper;
	}

	@Transactional(readOnly=true)
    public List<Person> getAuthors(R record, RequestContext request) throws ResponseContextException {
        return FeedOutputHelper.getAuthors(record, request);
    }

}