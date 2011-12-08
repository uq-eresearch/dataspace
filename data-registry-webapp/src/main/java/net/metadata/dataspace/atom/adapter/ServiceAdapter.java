package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:22:53 PM
 */
@Transactional
public class ServiceAdapter extends AbstractRecordAdapter<Service,ServiceVersion> {

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
	protected Entry getEntryFromEntity(ServiceVersion version,
			boolean isParentLevel) throws ResponseContextException {
		if (version == null) {
            throw new ResponseContextException(Constants.HTTP_STATUS_400, 400);
        }
		return getAdapterOutputHelper().getEntryFromService(
				(ServiceVersion) version, isParentLevel);
	}

}
