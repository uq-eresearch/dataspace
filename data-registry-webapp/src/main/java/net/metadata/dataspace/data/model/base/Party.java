package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.version.PartyVersion;
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
 * Date: 15/09/2010
 * Time: 3:32:39 PM
 */
@Entity
public class Party extends AbstractBaseEntity<PartyVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<PartyVersion> versions = new TreeSet<PartyVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PartyVersion published;

    public Party() {
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public String getSummary() {
        return this.published != null ? this.published.getSummary() : this.versions.first().getSummary();
    }

    public String getContent() {
        return this.published != null ? this.published.getContent() : this.versions.first().getContent();
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Set<String> getAuthors() {
        return this.published != null ? this.published.getAuthors() : this.versions.first().getAuthors();
    }

    public Set<Subject> getSubjects() {
        return this.published != null ? this.published.getSubjects() : this.versions.first().getSubjects();
    }

    public Set<Collection> getCollectorOf() {
        return this.published != null ? this.published.getCollectorOf() : this.versions.first().getCollectorOf();
    }

    public Set<Activity> getParticipantIn() {
        return this.published != null ? this.published.getParticipantIn() : this.versions.first().getParticipantIn();
    }

    public SortedSet<PartyVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<PartyVersion> versions) {
        this.versions = versions;
    }

    @Override
    public PartyVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(Version version) {
        this.published = (PartyVersion) version;
    }

    @Override
    public Version getWorkingCopy() {
        return this.versions.first();
    }


}
