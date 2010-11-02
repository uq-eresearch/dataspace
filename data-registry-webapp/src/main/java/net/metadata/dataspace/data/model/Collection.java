package net.metadata.dataspace.data.model;

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
public class Collection extends AbstractBaseEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<CollectionVersion> versions = new TreeSet<CollectionVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

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
        return versions.first().getTitle();
    }

    public String getSummary() {
        return versions.first().getSummary();
    }

    public Set<String> getAuthors() {
        return versions.first().getAuthors();
    }

    public String getContent() {
        return versions.first().getContent();
    }

    public Set<Subject> getSubjects() {
        return versions.first().getSubjects();
    }

    public Set<Party> getCollector() {
        return versions.first().getCollector();
    }

    public String getLocation() {
        return versions.first().getLocation();
    }

    public Set<Activity> getOutputOf() {
        return versions.first().getOutputOf();
    }

    public Set<Service> getSupports() {
        return versions.first().getSupports();
    }

}
