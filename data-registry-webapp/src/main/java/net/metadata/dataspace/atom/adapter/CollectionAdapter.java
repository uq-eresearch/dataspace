package net.metadata.dataspace.atom.adapter;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.context.ResponseContextException;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.version.CollectionVersion;

/**
 * User: alabri
 * Date: 24/09/2010
 * Time: 11:38:59 AM
 */
public class CollectionAdapter extends AbstractRecordAdapter<Collection,CollectionVersion> {

	@Override
	protected Class<Collection> getRecordClass() {
		return Collection.class;
	}

    @Override
    protected String getLinkTerm() {
    	return Constants.TERM_COLLECTION;
    }

    @Override
    protected String getTitle() {
        return Constants.TITLE_FOR_COLLECTIONS;
    }

    @Override
    public CollectionVersion assembleAndValidateVersionFromEntry(
    		Collection record, Entry entry) throws ResponseContextException
    {
    	CollectionVersion version =
    			super.assembleAndValidateVersionFromEntry(record,entry);
    	version.setRights(entry.getRights());
    	return version;
    }

	@Override
	protected Entry getEntryFromEntity(CollectionVersion version,
			boolean isParentLevel) throws ResponseContextException {
        if (version == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        }
        Entry entry = getAdapterOutputHelper().getEntryFromCollection(
				version, isParentLevel);
		return entry;
	}

}
