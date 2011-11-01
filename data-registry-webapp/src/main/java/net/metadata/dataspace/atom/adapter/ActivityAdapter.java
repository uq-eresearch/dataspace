package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.version.ActivityVersion;

import org.springframework.transaction.annotation.Transactional;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 3:24:08 PM
 */
@Transactional
public class ActivityAdapter extends AbstractRecordAdapter<Activity,ActivityVersion> {

	@Override
	protected Class<Activity> getRecordClass() {
		return Activity.class;
	}

	@Override
	protected String getLinkTerm() {
		return Constants.TERM_ACTIVITY;
	}

    @Override
    protected String getTitle() {
    	return Constants.TITLE_FOR_ACTIVITIES;
    }
}
