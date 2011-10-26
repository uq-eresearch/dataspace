package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.record.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:22:53 PM
 */
@Transactional
public class ServiceAdapter extends AbstractRecordAdapter<Service> {

	@Override
	protected Class<Service> getRecordClass() {
		return Service.class;
	}

	@Override
	protected String getLinkTerm() {
		return Constants.TERM_SERVICE;
	}

    @Override
    protected String getTitle() {
        return Constants.TITLE_FOR_SERVICES;
    }

	@Override
	protected String getBasePath() {
		return Constants.PATH_FOR_SERVICES;
	}

}
