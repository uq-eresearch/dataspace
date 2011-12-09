package net.metadata.dataspace.data.model.record;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.metadata.dataspace.data.model.version.ActivityVersion;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:30:02 AM
 */
@Entity
public class Activity extends AbstractRecordEntity<ActivityVersion> {

    private static final long serialVersionUID = 1L;

    public enum Type {
        PROJECT,
        PROGRAM
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActivityVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activity_same_as")
    private Set<Activity> sameAs = new HashSet<Activity>();

    public Activity() {
    }

    public Set<Collection> getHasOutput() {
        return getMostRecentVersion().getHasOutput();
    }

    public Set<Agent> getHasParticipant() {
        return getMostRecentVersion().getHasParticipants();
    }

    @Override
    public ActivityVersion getPublished() {
        return published;
    }

    @Override
    public ActivityVersion getWorkingCopy() {
        return getMostRecentVersion();
    }

    public void setPublished(ActivityVersion published) {
        this.published = published;
    }

    public Set<Activity> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Activity> sameAs) {
        this.sameAs = sameAs;
    }
}
