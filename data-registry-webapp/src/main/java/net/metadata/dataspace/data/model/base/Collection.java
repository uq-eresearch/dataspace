package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.version.CollectionVersion;
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
 * Time: 3:32:27 PM
 */
@Entity
public class Collection extends AbstractBaseEntity<CollectionVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<CollectionVersion> versions = new TreeSet<CollectionVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CollectionVersion published;

    public Collection() {

    }

    public SortedSet<CollectionVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<CollectionVersion> versions) {
        this.versions = versions;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public Set<String> getAuthors() {
        return this.published != null ? this.published.getAuthors() : this.versions.first().getAuthors();
    }

    public String getContent() {
        return this.published != null ? this.published.getDescription() : this.versions.first().getDescription();
    }

    public Set<Subject> getSubjects() {
        return this.published != null ? this.published.getSubjects() : this.versions.first().getSubjects();
    }

    public Set<Party> getCollector() {
        return this.published != null ? this.published.getCollector() : this.versions.first().getCollector();
    }

    public String getLocation() {
        return this.published != null ? this.published.getLocation() : this.versions.first().getLocation();
    }

    public Set<Activity> getOutputOf() {
        return this.published != null ? this.published.getOutputOf() : this.versions.first().getOutputOf();
    }

    public Set<Service> getSupports() {
        return this.published != null ? this.published.getSupports() : this.versions.first().getSupports();
    }

    @Override
    public CollectionVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(Version published) {
        this.published = (CollectionVersion) published;
    }

    @Override
    public Version getWorkingCopy() {
        return this.versions.first();
    }
}
