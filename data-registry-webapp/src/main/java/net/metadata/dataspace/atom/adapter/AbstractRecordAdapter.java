package net.metadata.dataspace.atom.adapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.persistence.EntityManager;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterInputHelper;
import net.metadata.dataspace.atom.util.AdapterOutputHelper;
import net.metadata.dataspace.atom.util.FeedOutputHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.data.access.RecordDao;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.User;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.history.FeedPagingHelper;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractRecordAdapter<R extends Record<V>, V extends Version<R>> extends
        AbstractEntityCollectionAdapter<R> {

	protected enum KnownContentTypes {
		ATOM("atom", "application/atom+xml;type=feed", "application/atom+xml;type=entry"),
		HTML("html", "text/html", "text/html"),
		RDF("rdf", null, "application/rdf+xml"),
		RIFCS("xml", null, "application/vnd.ands.rifcs+xml");

		public static Set<KnownContentTypes> getEntryTypes() {
			Set<KnownContentTypes> types = new LinkedHashSet<KnownContentTypes>();
			for (KnownContentTypes type : Arrays.asList(values())) {
				if (type.entryMimeType != null) {
					types.add(type);
				}
			}
			return types;
		}

		public static Set<KnownContentTypes> getFeedTypes() {
			Set<KnownContentTypes> types = new LinkedHashSet<KnownContentTypes>();
			for (KnownContentTypes type : Arrays.asList(values())) {
				if (type.feedMimeType != null) {
					types.add(type);
				}
			}
			return types;
		}

		public static KnownContentTypes matchAccept(String acceptType) {
			try {
				return matchAccept(new MimeType(acceptType));
			} catch (MimeTypeParseException e) {
				return null;
			}
		}

		public static KnownContentTypes matchAccept(MimeType acceptType) {
			for (AbstractRecordAdapter.KnownContentTypes type : Arrays.asList(values())) {
				if (type.feedMimeType != null &&
						type.feedMimeType.match(acceptType)) {
					return type;
				}
				if (type.entryMimeType != null &&
						type.entryMimeType.match(acceptType)) {
					return type;
				}
			}
			return null;
		}

		public static KnownContentTypes matchUri(IRI uri) {
			for (AbstractRecordAdapter.KnownContentTypes type : Arrays.asList(values())) {
				if (uri.getPath().endsWith("."+type.fileExt)) {
					return type;
				}
			}
			return null;
		}

		public final String fileExt;
		public final MimeType feedMimeType;
		public final MimeType entryMimeType;

		private KnownContentTypes(String fileExt, String feedMimeType, String entryMimeType) {
			this.fileExt = fileExt;
			try {
				this.feedMimeType = feedMimeType == null ?
						null : new MimeType(feedMimeType);
				this.entryMimeType = entryMimeType == null ?
						null : new MimeType(entryMimeType);
			} catch (MimeTypeParseException e) {
				// Should never happen
				throw new RuntimeException(e);
			}
		}

		public IRI getUri(IRI base) {
			return new IRI(base.getScheme(), base.getAuthority(),
					base.getPath() +"."+ this.fileExt,
					base.getQuery(), base.getFragment());
		}

	}
	public static final int DEFAULT_PAGE_SIZE = 20;

    protected Logger logger = Logger.getLogger(getClass());

    private AdapterInputHelper adapterInputHelper;
    private AdapterOutputHelper adapterOutputHelper;
    private AuthenticationManager authenticationManager;
    private AuthorizationManager<User> authorizationManager;
    private DaoManager daoManager;
    private EntityCreator entityCreator;
    private FeedOutputHelper feedOutputHelper;
    private RecordDao<R,V> dao;

    public AbstractRecordAdapter() {
        super();
    }

    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException {
		KnownContentTypes representationMimeType =
				getRepresentationMimeType(request);

		if (!KnownContentTypes.getFeedTypes().contains(representationMimeType)) {
			System.out.println(KnownContentTypes.getFeedTypes()+" does not contain "+representationMimeType);
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
		}

        R latestRecord = getDao().getMostRecentUpdated();
        if (latestRecord != null) {
            getDao().refresh(latestRecord);
            feed.setUpdated(latestRecord.getUpdated());
        } else {
            //TODO what would the date be if the feed is empty??
            feed.setUpdated(new Date());
        }

        addSelfAndAlternateLinks(feed, representationMimeType);

        feed.setTitle(getTitle());

        Iterable<R> entries = getEntries(request);
        if (entries != null) {
	        for (R entryObj : entries) {
	            Entry e = feed.addEntry();
	            IRI feedIri = new IRI(getFeedIriForEntry(entryObj, request));
	            addEntryDetails(request, e, feedIri, entryObj);
	            getFeedOutputHelper().setPublished(entryObj, e);
	            if (isMediaEntry(entryObj)) {
	                addMediaContent(feedIri, e, entryObj, request);
	            } else {
	                addContent(e, entryObj, request);
	                Link typeLink = e.addLink(getLinkTerm(), Constants.REL_TYPE);
	                typeLink.setTitle(getTitle());
	            }
	        }
        }

        addPagingLinks(feed, request);
    }

	protected void addPagingLinks(Feed feed, RequestContext request) {
		int pageNumber = getPageNumber(request);
        int pageSize = getPageSize(request);
        FeedPagingHelper.setComplete(feed, false);

        IRI base = feed.getSelfLink().getHref();
        if (pageSize != DEFAULT_PAGE_SIZE) {
    		String queryString = base.getQuery() == null ? "" : base.getQuery()+"&";
            queryString += "size="+pageSize;
            base = new IRI(base.getScheme(), base.getHost(),
    				base.getPath(),
    				queryString, base.getFragment());
        }

        FeedPagingHelper.setCurrent(feed, getHrefForPage(base,pageNumber));
        if (pageNumber > 1) {
        	FeedPagingHelper.setPrevious(feed, getHrefForPage(base,pageNumber-1));
        }
        if (feed.getEntries().size() == pageSize) {
        	FeedPagingHelper.setNext(feed, getHrefForPage(base,pageNumber+1));
        }
	}

	protected void addSelfAndAlternateLinks(Feed feed,
			KnownContentTypes representationMimeType)
	{
        IRI baseUri = new IRI(
        		RegistryApplication.getApplicationContext().getUriPrefix() +
        		getBasePath());

        for (KnownContentTypes type : KnownContentTypes.getFeedTypes()) {
        	String rel = type == representationMimeType ?
        			Link.REL_SELF : Link.REL_ALTERNATE;
        	feed.getLink(rel).discard();
        	feed.addLink(type.getUri(baseUri).toString(), rel,
        			type.feedMimeType.toString(),
        			null, null, -1);
        }
	}

	@Override
    @Transactional(propagation=Propagation.REQUIRED)
    public ResponseContext deleteEntry(RequestContext request) {
        try {
            enforceAuthentication(request);
            R record = getExistingRecord(request);
            if (record == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            getDao().refresh(record);
            if (!record.isActive()) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            User user = getAuthenticationManager().getCurrentUser(request);
            if (!getAuthorizationManager().getAccessLevelForInstance(user, record).canDelete()) {
                throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
            }
            try {
                getDao().softDelete(record.getUriKey());
            } catch (Throwable th) {
                throw new ResponseContextException(500, th);
            }
            return OperationHelper.createResponse(200, Constants.HTTP_STATUS_200);
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

	@Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteEntry(String key, RequestContext requestContext) throws ResponseContextException {
        getDao().softDelete(key);
    }

    protected void enforceAuthentication(RequestContext request)
    		throws ResponseContextException
    {
        if (getAuthenticationManager().getCurrentUser(request) == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        }
    }

    protected void ensureRequestIsAtom(RequestContext request)
			throws ResponseContextException {
		MimeType knownMimeType = request.getContentType();
        if (KnownContentTypes.matchAccept(knownMimeType) != KnownContentTypes.ATOM) {
			System.out.println(knownMimeType+" is not atom.");
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
        }
	}

    @Override
    public String[] getAccepts(RequestContext requestContext) {
        return new String[]{Constants.MIME_TYPE_ATOM_ENTRY};
    }

    public AdapterInputHelper getAdapterInputHelper() {
        return adapterInputHelper;
    }

    public AdapterOutputHelper getAdapterOutputHelper() {
        return adapterOutputHelper;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    @Override
    public String getAuthor(RequestContext requestContext) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix();
    }

    public AuthorizationManager<User> getAuthorizationManager() {
        return authorizationManager;
    }

    public List<Person> getAuthors(R record, RequestContext request) throws ResponseContextException {
        return getFeedOutputHelper().getAuthors(record, request);
    }

    protected String getBasePath() {
		return getHref();
	}

    @Override
    public Object getContent(R entry, RequestContext request) throws ResponseContextException {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getContent());
        return content;
    }

    public RecordDao<R,V> getDao() {
        return dao;
    }

    public DaoManager getDaoManager() {
		return daoManager;
	}

    public EntityCreator getEntityCreator() {
        return entityCreator;
    }

    @Override
    public Iterable<R> getEntries(RequestContext requestContext) throws ResponseContextException {
    	User user = getAuthenticationManager().getCurrentUser(requestContext);
    	int pageNumber = getPageNumber(requestContext);
    	int pageSize = getPageSize(requestContext);
	    List<R> list;
	    if (getAuthorizationManager().canAccessWorkingCopy(user, Collection.class)) {
	        list = getDao().getActive(pageSize,pageNumber);
	    } else {
	        list = getDao().getPublished(pageSize,pageNumber);
	    }
        return list;
    }

    @Override
    public ResponseContext getEntry(RequestContext request) {
        try {
            R record = getExistingRecord(request);
            if (record == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            getDao().refresh(record);
            if (!record.isActive()) {
            	throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            String versionKey = OperationHelper.getEntryVersionID(request);
            User user = getAuthenticationManager().getCurrentUser(request);
            Version version;
            if (versionKey == null) {
                if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate() && record.getPublished() == null) {
                    version = record.getWorkingCopy();
                } else {
                    version = record.getPublished();
                }

            } else {
            	if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
                    if (versionKey.equals(Constants.TARGET_TYPE_VERSION_HISTORY)) {
                        Feed versionHistoryFeed = getFeedOutputHelper().createVersionFeed(request);
                        ResponseContext versionHistoryFeed1 = getFeedOutputHelper().getVersionHistoryFeed(request, versionHistoryFeed, record, getRecordClass());
                        return versionHistoryFeed1;
                    } else {
                        version = getDao().getByVersion(record.getUriKey(), versionKey);
                    }
                } else {
                    throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
                }
            }
            if (version == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            Entry entry = adapterOutputHelper.getEntryFromEntity(version, versionKey == null);
            return adapterOutputHelper.getContextResponseForGetEntry(request, entry, getRecordClass());
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

    @Override
    public R getEntry(String key, RequestContext request) throws ResponseContextException {
        R entry = getDao().getByKey(key);
        if (entry != null) {
            getDao().refresh(entry);
        }
        return entry;

    }

    public R getExistingRecord(RequestContext request)
    		throws ResponseContextException
    {
        String uriKey = OperationHelper.getEntryID(request);
        return getDao().getByKey(uriKey);
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
    	ResponseContext response = super.getFeed(request);

        if (getRepresentationMimeType(request) == KnownContentTypes.HTML) {
            return feedOutputHelper.getHtmlRepresentationOfFeed(request, response, getRecordClass());
        } else {
        	return response;
        }
    }

    public FeedOutputHelper getFeedOutputHelper() {
        return feedOutputHelper;
    }

    /**
     * Retrieves the FOM Entry object from the request payload.
     */
    private Entry getFomEntryFromRequest(RequestContext request) throws ResponseContextException {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();
        Document<Entry> entry_doc;
        try {
            entry_doc = (Document<Entry>) request.getDocument(parser).clone();
            if (entry_doc == null) {
                return null;
            }
            return entry_doc.getRoot();
        } catch (ParseException e) {
            throw new ResponseContextException(400, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
    }

    /**
     * Get href (but do it synchronously, because otherwise we get
     * java.util.ConcurrentModificationException from the underlying
     * HashMap context.
     */
	@Override
	public synchronized String getHref(RequestContext request) {
		return super.getHref(request);
	}

    protected String getHrefForPage(IRI base, int pageNumber) {
		String queryString = base.getQuery() == null ? "" : base.getQuery()+"&";
        IRI uri = new IRI(base.getScheme(), base.getHost(),
				base.getPath(),
				queryString+"page="+pageNumber, base.getFragment());
		return uri.toString();
	}

    @Override
    public String getId(R record) {
        return RegistryApplication.getApplicationContext().getUriPrefix() + getBasePath() + "/" + record.getUriKey();
    }

    @Override
    public String getId(RequestContext requestContext) {
        return RegistryApplication.getApplicationContext().getUriPrefix() + getBasePath();
    }

    protected abstract String getLinkTerm();

    @Override
    public String getName(R record) throws ResponseContextException {
        return RegistryApplication.getApplicationContext().getUriPrefix() + getBasePath() + "/" + record.getUriKey();
    }

    protected int getPageNumber(RequestContext requestContext) {
	    try {
	    	return Integer.parseInt(requestContext.getParameter("page"));
	    } catch (Exception e) {
	    	return 1;
	    }
    }

    protected int getPageSize(RequestContext requestContext) {
	    try {
	    	return Integer.parseInt(requestContext.getParameter("size"));
	    } catch (Exception e) {
	    	return DEFAULT_PAGE_SIZE;
	    }
    }

    protected abstract Class<R> getRecordClass();

    protected KnownContentTypes getRepresentationMimeType(RequestContext request) {
		KnownContentTypes representationMimeType = KnownContentTypes.matchUri(request.getUri());
        if (representationMimeType != null) {
        	return representationMimeType;
        }
        String acceptHeader = request.getAccept();
        if (acceptHeader != null) {
            representationMimeType = KnownContentTypes.matchAccept(acceptHeader);
            if (representationMimeType != null)
            	return representationMimeType;
        }
    	return KnownContentTypes.HTML;
	}

    protected abstract String getTitle();

    @Override
    public String getTitle(R record) throws ResponseContextException {
        return record.getTitle();
    }

	@Override
    public String getTitle(RequestContext request) {
        return getTitle();
    }

    @Override
    public Date getUpdated(R record) throws ResponseContextException {
        return record.getUpdated();
    }

    /**
     * For agents it is possible to match an existing record based on email
     * address. In those cases, POST requests will reinstate a deleted record
     * but will not overwrite an active record.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseContext postEntry(RequestContext request) {
    	try {
    		enforceAuthentication(request);
    		R existingRecord = getExistingRecord(request);
    		// If it doesn't exist, create a new record
    		if (existingRecord == null) {
    			Entry createdEntry = processPostRequest(request);
                return adapterOutputHelper.getContextResponseForPost(createdEntry);
    		}
    		// If active, this is a conflict
    		if (existingRecord.isActive()) {
				throw new ResponseContextException(
	            		"Entry already exists: "+existingRecord, 409);
    		}
    		// Otherwise, reinstate the record
    		Entry reinstatedEntry = processPutRequest(request);
            return adapterOutputHelper.getContextResponseForPost(reinstatedEntry);
    	} catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

    @Override
    public R postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content, RequestContext requestContext) throws ResponseContextException {
        logger.warn("Method not supported.");
        return null;
    }

    /**
     * We do not support media posting
     */
    @Override
    public ResponseContext postMedia(RequestContext request) {
        try {
            enforceAuthentication(request);
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

	protected Entry processPostRequest(RequestContext request)
    		throws ResponseContextException
    {
        EntityManager entityManager = getDaoManager().getEntityManagerSource().getEntityManager();
    	ensureRequestIsAtom(request);
        Entry entry = getEntryFromRequest(request);
        Record record = getEntityCreator().getNextRecord(getRecordClass());
        Version version = adapterInputHelper.assembleAndValidateVersionFromEntry(record, entry);
        if (version == null) {
            throw new ResponseContextException("Version is null", 400);
        }
        try {
            Source source = adapterInputHelper.assembleAndValidateSourceFromEntry(entry);
            if (source.getId() == null) {
                entityManager.persist(source);
            }
            version.setParent(record);
            Date now = new Date();
            version.setUpdated(now);
            List<Person> authors = entry.getSource().getAuthors();
            adapterInputHelper.addDescriptionAuthors(version, authors, request);
            version.setSource(source);
            //TODO these values (i.e. rights, license) should come from the entry
            record.setLicense(RegistryApplication.getApplicationContext().getRegistryLicense());
            record.setRights(RegistryApplication.getApplicationContext().getRegistryRights());
            record.getVersions().add(version);
            record.setUpdated(now);
            entityManager.persist(version);
            entityManager.persist(record);
            adapterInputHelper.addRelations(entry, version,
            		getAuthenticationManager().getCurrentUser(request));
            // Return created entry
            return adapterOutputHelper.getEntryFromEntity(version, true);
        } catch (Exception th) {
            logger.warn("Invalid Entry, Rolling back database", th);
            throw new ResponseContextException(th.getMessage(), 400);
        }
    }

    protected Entry processPutRequest(RequestContext request)
			throws ResponseContextException
	{
		logger.info("Updating Entry");
		ensureRequestIsAtom(request);
	    Entry entry = getFomEntryFromRequest(request);
	    R record = getExistingRecord(request);
	    if (record == null) {
	        throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
	    }
        getDao().refresh(record);
        if (!record.isActive()) {
        	((AbstractRecordEntity<V>) record).setActive(true);
        }
        V version = (V) adapterInputHelper.assembleAndValidateVersionFromEntry(record, entry);
        if (version == null) {
            throw new ResponseContextException("Version is null", 400);
        }
        User user = getAuthenticationManager().getCurrentUser(request);
        if (!getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        }
        EntityManager entityManager = getDaoManager().getEntityManagerSource().getEntityManager();
        try {
            Source source = adapterInputHelper.assembleAndValidateSourceFromEntry(entry);
            if (source.getId() == null) {
                entityManager.persist(source);
            }
            record.getVersions().add(version);
            version.setParent(record);
            adapterInputHelper.addRelations(entry, version, user);
            record.setUpdated(new Date());
            List<Person> authors = entry.getSource().getAuthors();
            adapterInputHelper.addDescriptionAuthors(version, authors, request);
            version.setSource(source);
            entityManager.persist(version);
            entityManager.merge(record);
            // Return updated entry (with parent-level location)
            // return adapterOutputHelper.getEntryFromEntity(version, false);
            return adapterOutputHelper.getEntryFromEntity(version, true);
        } catch (Exception th) {
            logger.fatal("Invalid Entry, Rolling back database", th);
            throw new ResponseContextException("Invalid Entry, Rolling back database", 400);
        }
	}

    @Override
    public void putEntry(R entry, String title, Date updated, List<Person> authors, String summary, Content content, RequestContext request) throws ResponseContextException {
        logger.warn("Method not supported.");
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public ResponseContext putEntry(RequestContext request) {
        try {
            enforceAuthentication(request);
            Entry updatedEntry = processPutRequest(request);
            return adapterOutputHelper.getContextResponseForPut(updatedEntry);
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

    /**
     * We do not support media putting
     */
    @Override
    public ResponseContext putMedia(RequestContext request) {
        try {
            enforceAuthentication(request);
            throw new ResponseContextException(Constants.HTTP_STATUS_415, 415);
        } catch (ResponseContextException e) {
            return OperationHelper.createErrorResponse(e);
        }
    }

    public void setAdapterInputHelper(AdapterInputHelper adapterInputHelper) {
        this.adapterInputHelper = adapterInputHelper;
    }

    public void setAdapterOutputHelper(AdapterOutputHelper adapterOutputHelper) {
        this.adapterOutputHelper = adapterOutputHelper;
    }

    @Required
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Required
    public void setAuthorizationManager(AuthorizationManager<User> authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

	public void setDao(RecordDao<R,V> dao) {
        this.dao = dao;
    }

	public void setDaoManager(DaoManager daoManager) {
		this.daoManager = daoManager;
	}

	public void setEntityCreator(EntityCreator entityCreator) {
        this.entityCreator = entityCreator;
    }

    @Required
    public void setFeedOutputHelper(FeedOutputHelper feedOutputHelper) {
        this.feedOutputHelper = feedOutputHelper;
    }

}
