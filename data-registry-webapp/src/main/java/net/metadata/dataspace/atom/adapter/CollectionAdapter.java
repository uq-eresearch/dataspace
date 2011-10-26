package net.metadata.dataspace.atom.adapter;

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
	protected String getBasePath() {
		return Constants.PATH_FOR_COLLECTIONS;
	}

}
