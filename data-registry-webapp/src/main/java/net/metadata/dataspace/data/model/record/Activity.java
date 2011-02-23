package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.version.ActivityVersion;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activities_description_creators")
    private Set<Agent> creators = new HashSet<Agent>();

    public Activity() {
    }

    public SortedSet<ActivityVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ActivityVersion> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public String getContent() {
        return this.published != null ? this.published.getDescription() : this.versions.first().getDescription();
    }

    public Set<Collection> getHasOutput() {
        return this.published != null ? this.published.getHasOutput() : this.versions.first().getHasOutput();
    }

    public Set<Agent> getHasParticipant() {
        return this.published != null ? this.published.getHasParticipants() : this.versions.first().getHasParticipants();
    }

    public Set<String> getAuthors() {
        return this.published != null ? this.published.getAuthors() : this.versions.first().getAuthors();
    }

    @Override
    public ActivityVersion getPublished() {
        return published;
    }

    @Override
    public net.metadata.dataspace.data.model.Version getWorkingCopy() {
        return this.versions.first();
    }

    public void setPublished(net.metadata.dataspace.data.model.Version published) {
        this.published = (ActivityVersion) published;
    }

    public Set<Activity> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Activity> sameAs) {
        this.sameAs = sameAs;
    }
}
