package net.metadata.dataspace.atom.adapter;

import java.util.List;

import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.HttpMethodHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.RegistryDao;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.User;

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

	private AuthenticationManager authenticationManager;
	private AuthorizationManager<User> authorizationManager;
	private HttpMethodHelper httpMethodHelper;
	private FeedOutputHelper feedOutputHelper;
    private RegistryDao<R> dao;

    public RegistryDao<R> getDao() {
		return dao;
	}

	public void setDao(RegistryDao<R> dao) {
		this.dao = dao;
	}

	public AbstractRecordAdapter() {
		super();
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public AuthorizationManager<User> getAuthorizationManager() {
		return authorizationManager;
	}

	@Transactional(readOnly=true)
    public List<Person> getAuthors(R record, RequestContext request) throws ResponseContextException {
        return getFeedOutputHelper().getAuthors(record, request);
    }

	public FeedOutputHelper getFeedOutputHelper() {
		return feedOutputHelper;
	}

	public HttpMethodHelper getHttpMethodHelper() {
		return httpMethodHelper;
	}

	public List<R> getRecords(RequestContext request, Class<?> clazz) {
        User user = getAuthenticationManager().getCurrentUser(request);
        List<R> list;
        if (getAuthorizationManager().canAccessWorkingCopy(user, Collection.class)) {
            list = getDao().getAllPublished();
            list.addAll((java.util.Collection<? extends R>) getDao().getAllUnpublished());
        } else {
        	list = getDao().getAllPublished();
        }
        return list;
    }

	@Required
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Required
	public void setAuthorizationManager(AuthorizationManager<User> authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	@Required
	public void setFeedOutputHelper(FeedOutputHelper feedOutputHelper) {
		this.feedOutputHelper = feedOutputHelper;
	}

	@Required
	public void setHttpMethodHelper(HttpMethodHelper httpMethodHelper) {
		this.httpMethodHelper = httpMethodHelper;
	}

}