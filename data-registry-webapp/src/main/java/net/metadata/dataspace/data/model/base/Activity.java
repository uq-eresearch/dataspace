package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.version.ActivityVersion;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:30:02 AM
 */
@Entity
public class Activity extends AbstractBaseEntity<ActivityVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<ActivityVersion> versions = new TreeSet<ActivityVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActivityVersion published;

    public Activity() {
    }

    public SortedSet<ActivityVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ActivityVersion> versions) {
        this.versions = versions;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return versions.first().getTitle();
    }

    public String getSummary() {
        return versions.first().getSummary();
    }

    public String getContent() {
        return versions.first().getContent();
    }

    public Set<Collection> getHasOutput() {
        return versions.first().getHasOutput();
    }

    public Set<Party> getHasParticipant() {
        return versions.first().getHasParticipant();
    }

    public Set<String> getAuthors() {
        return versions.first().getAuthors();
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
}