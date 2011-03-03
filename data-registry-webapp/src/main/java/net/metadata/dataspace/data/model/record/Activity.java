package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.context.Source;
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activities_description_authors")
    private Set<Agent> authors = new HashSet<Agent>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source locatedOn;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source source;

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

    public void setPublished(net.metadata.dataspace.data.model.Version published) {
        this.published = (ActivityVersion) published;
    }

    public Set<Activity> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Activity> sameAs) {
        this.sameAs = sameAs;
    }

    @Override
    public Set<Agent> getAuthors() {
        return this.authors;
    }

    @Override
    public void setAuthors(Set<Agent> authors) {
        this.authors = authors;
    }

    public Source getLocatedOn() {
        return locatedOn;
    }

    @Override
    public void setLocatedOn(Source locatedOn) {
        this.locatedOn = locatedOn;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public void setSource(Source source) {
        this.source = source;
    }
}
