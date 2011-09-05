package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.version.ActivityVersion;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:30:02 AM
 */
@Entity
public class Activity extends AbstractRecordEntity<ActivityVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<ActivityVersion> versions = new TreeSet<ActivityVersion>();


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActivityVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activity_same_as")
    private Set<Activity> sameAs = new HashSet<Activity>();

    public Activity() {
    }

    public SortedSet<ActivityVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ActivityVersion> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return this.versions.first().getTitle();
    }

    public String getContent() {
        return this.versions.first().getDescription();
    }

    public Set<Collection> getHasOutput() {
        return this.versions.first().getHasOutput();
    }

    public Set<Agent> getHasParticipant() {
        return this.versions.first().getHasParticipants();
    }

    @Override
    public ActivityVersion getPublished() {
        return published;
    }

    @Override
    public ActivityVersion getWorkingCopy() {
        return this.versions.first();
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
