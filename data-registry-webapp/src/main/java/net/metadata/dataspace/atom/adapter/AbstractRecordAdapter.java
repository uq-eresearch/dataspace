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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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
import net.metadata.dataspace.data.model.UnknownTypeException;
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
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public abstract class AbstractRecordAdapter<R extends Record<V>, V extends Version<R>>
		extends AbstractEntityCollectionAdapter<R>
		implements VersionAssembler<R,V>
{

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
            feed.setUpdated(latestRecord.getUpdated().getTime());
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

        FeedPagingHelper.setFirst(feed, base.toString());
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

	private void addType(V version, Entry entry) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException("Version is null", 400);
        } else {
            List<Link> links = entry.getLinks(Constants.REL_TYPE);
            if (links.isEmpty()) {
                throw new ResponseContextException("Entry missing Type", 400);
            } else if (links.size() > 1) {
                throw new ResponseContextException("Entry assigned to more than one type", 400);
            } else {
                Link typeLink = links.get(0);
                String entryType = typeLink.getTitle();
                if (entryType == null) {
                    throw new ResponseContextException("Entry type is missing label", 400);
                } else {
                    entryType = entryType.toUpperCase();
                    try {
                    	version.setType(entryType);
                    } catch (UnknownTypeException e) {
                        throw new ResponseContextException("Entry type is invalid: "+entryType, 400);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	public V assembleAndValidateVersionFromEntry(R record, Entry entry) throws ResponseContextException {
        if (entry == null) {
            throw new ResponseContextException("Empty Atom entry", 400);
        } else if(!ProviderHelper.isValidEntry(entry)) {
            throw new ResponseContextException("Invalid Atom entry", 400);
        } else {
            String content = entry.getContent();
            if (content == null) {
                throw new ResponseContextException("Content is null", 400);
            }
            V version = (V) entityCreator.getNextVersion(record);
            if (version == null) {
                throw new ResponseContextException("Version is null", 400);
            }
            version.setTitle(entry.getTitle());
            version.setDescription(content);
            addType(version, entry);
            return version;
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
            V version;
            if (getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
                if (versionKey == null) {
                    version = record.getWorkingCopy();
                } else {
                	if (versionKey.equals(Constants.TARGET_TYPE_VERSION_HISTORY)) {
                        Feed versionHistoryFeed = getFeedOutputHelper().createVersionFeed(request);
                        ResponseContext versionHistoryFeed1 = getFeedOutputHelper().getVersionHistoryFeed(request, versionHistoryFeed, record, getRecordClass());
                        return versionHistoryFeed1;
                    } else {
                        version = getDao().getByVersion(record.getUriKey(), versionKey);
                    }
                }
            } else {
            	if (versionKey != null || OperationHelper.getViewRepresentation(request) != null) {
                    throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
            	}
            	version = record.getPublished();
            }
            if (version == null) {
                throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
            }
            Entry entry = getEntryFromEntity(version, versionKey == null);
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

    protected abstract Entry getEntryFromEntity(V version, boolean isParentLevel)
    		throws ResponseContextException;

    public R getExistingRecord(RequestContext request)
    		throws ResponseContextException
    {
        String uriKey = OperationHelper.getEntryID(request);
        return getDao().getByKey(uriKey);
    }

    @Override
    public ResponseContext getFeed(RequestContext request) {
    	ResponseContext response = super.getFeed(request);

    	if (OperationHelper.getViewRepresentation(request) != null) {
            try {
				enforceAuthentication(request);
			} catch (ResponseContextException e) {
	            return OperationHelper.createErrorResponse(e);
			}
    	}

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
        IRI uri = new IRI(base.getScheme(), base.getUserInfo(), base.getHost(),
				base.getPort(), base.getPath(),
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
        return record.getUpdated().getTime();
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
                String uri = request.getResolvedUri().toString() + "/" + existingRecord.getUriKey();

				throw new ResponseContextException(
	            		uri + " already exists", 409);
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
        R record = (R) getEntityCreator().getNextRecord(getRecordClass());
        entityManager.persist(record);
        entityManager.flush();
        try {
            Source source = adapterInputHelper.assembleAndValidateSourceFromEntry(entry);
            if (source.getId() == null) {
                entityManager.persist(source);
            }
            List<Person> authors = entry.getSource().getAuthors();
            V version = assembleAndValidateVersionFromEntry(record, entry);
            adapterInputHelper.addDescriptionAuthors(version, authors, request);
            version.setSource(source);
            // TODO: the source rights and license should come from the entry
            record.setSourceLicense(RegistryApplication.getApplicationContext().getRegistryLicense());
            record.setSourceRights(RegistryApplication.getApplicationContext().getRegistryRights());
            adapterInputHelper.addRelations(entry, version,
            		getAuthenticationManager().getCurrentUser(request));
            entityManager.persist(version);
            // Refresh from database
            entityManager.flush();
            version = record.getWorkingCopy();
            // Return created entry
            return getEntryFromEntity(version, true);
        } catch (ConstraintViolationException e) {
            logger.warn("Invalid Entry, Rolling back database", e);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	throw new ResponseContextException(
            		buildConstraintViolationMessage(e), 400);
        } catch (Exception th) {
            logger.warn("Invalid Entry, Rolling back database", th);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ResponseContextException(th.getMessage(), 400);
        }
    }

    private String buildConstraintViolationMessage(
			ConstraintViolationException e) {
    	StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
        	sb.append(cv.getPropertyPath());
        	sb.append(" ");
        	sb.append(cv.getMessage());
        	sb.append(": ");
        	sb.append(cv.getInvalidValue());
        	sb.append("\n");
        }
		return sb.toString();
	}

	protected Entry processPutRequest(RequestContext request)
			throws ResponseContextException
	{
        EntityManager entityManager = getDaoManager().getEntityManagerSource().getEntityManager();
		logger.info("Updating Entry");
		ensureRequestIsAtom(request);
	    // Find record from Atom
		Entry entry = getFomEntryFromRequest(request);
	    R record = getExistingRecord(request);
	    if (record == null) {
	        throw new ResponseContextException(Constants.HTTP_STATUS_404, 404);
	    }
        // Check the user has access
        User user = getAuthenticationManager().getCurrentUser(request);
        if (!getAuthorizationManager().getAccessLevelForInstance(user, record).canUpdate()) {
            throw new ResponseContextException(Constants.HTTP_STATUS_401, 401);
        }
	    // Ensure the record is up-to-date
        getDao().refresh(record);
        // Reactivate record if necessary
        if (!record.isActive()) {
        	((AbstractRecordEntity<V>) record).setActive(true);
        }
        entityManager.flush();
        // Save record (and thus cascade through and save version)
//        try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {}
        try {
            Source source = adapterInputHelper.assembleAndValidateSourceFromEntry(entry);
            if (source.getId() == null) {
                entityManager.persist(source);
            }
            entityManager.flush();
            // Assemble and add new version to record
            V version = assembleAndValidateVersionFromEntry(record, entry);
            adapterInputHelper.addRelations(entry, version, user);
            List<Person> authors = entry.getSource().getAuthors();
            adapterInputHelper.addDescriptionAuthors(version, authors, request);
            version.setSource(source);
            entityManager.persist(version);
            // Refresh from database
            entityManager.flush();
            version = record.getWorkingCopy();
            // Return updated entry (with parent-level location)
            // return adapterOutputHelper.getEntryFromEntity(version, false);
            return getEntryFromEntity(version, true);
        } catch (ConstraintViolationException e) {
            logger.warn("Invalid Entry, Rolling back database", e);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	throw new ResponseContextException(
            		buildConstraintViolationMessage(e), 400);
        } catch (Exception th) {
            logger.fatal("Invalid Entry, Rolling back database", th);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
