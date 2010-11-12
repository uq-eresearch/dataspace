package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.version.ServiceVersion;
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
 * Time: 10:30:19 AM
 */
@Entity
public class Service extends AbstractBaseEntity<ServiceVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<ServiceVersion> versions = new TreeSet<ServiceVersion>();

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    public Service() {
    }

    public SortedSet<ServiceVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ServiceVersion> versions) {
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

    public Set<Collection> getSupportedBy() {
        return versions.first().getSupportedBy();
    }

    public String getLocation() {
        return versions.first().getLocation();
    }


    public Set<String> getAuthors() {
        return versions.first().getAuthors();
    }

}
