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
        return versions.first().getTitle();
    }

    public String getSummary() {
        return versions.first().getSummary();
    }

    public String getContent() {
        return versions.first().getContent();
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Set<String> getAuthors() {
        return versions.first().getAuthors();
    }

    public Set<Subject> getSubjects() {
        return versions.first().getSubjects();
    }

    public Set<Collection> getCollectorOf() {
        return versions.first().getCollectorOf();
    }

    public Set<Activity> getParticipantIn() {
        return versions.first().getParticipantIn();
    }

    public SortedSet<PartyVersion> getVersions() {
        return versions;
    }

    @Override
    public PartyVersion getPublished() {
        return published;
    }

    public void setPublished(PartyVersion version) {
        this.published = version;
    }

    @Override
    public Version getWorkingCopy() {
        return this.versions.first();
    }

    public void setVersions(SortedSet<PartyVersion> versions) {
        this.versions = versions;
    }


}
