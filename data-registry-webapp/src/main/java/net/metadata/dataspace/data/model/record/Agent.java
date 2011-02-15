package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.resource.Subject;
import net.metadata.dataspace.data.model.version.AgentVersion;
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
public class Agent extends AbstractRecordEntity<AgentVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<AgentVersion> versions = new TreeSet<AgentVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AgentVersion published;

    public Agent() {
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public String getContent() {
        return this.published != null ? this.published.getDescription() : this.versions.first().getDescription();
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

    public SortedSet<AgentVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<AgentVersion> versions) {
        this.versions = versions;
    }

    @Override
    public AgentVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(Version version) {
        this.published = (AgentVersion) version;
    }

    @Override
    public Version getWorkingCopy() {
        return this.versions.first();
    }


}
