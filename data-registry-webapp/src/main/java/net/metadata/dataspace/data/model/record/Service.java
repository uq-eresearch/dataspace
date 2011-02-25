package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.version.ServiceVersion;
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
 * Time: 10:30:19 AM
 */
@Entity
public class Service extends AbstractRecordEntity<ServiceVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<ServiceVersion> versions = new TreeSet<ServiceVersion>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "service_same_as")
    private Set<Service> sameAs = new HashSet<Service>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "service_description_creators")
    private Set<Agent> creators = new HashSet<Agent>();

    public Service() {
    }

    public SortedSet<ServiceVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ServiceVersion> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public String getContent() {
        return this.published != null ? this.published.getDescription() : this.versions.first().getDescription();
    }

    public Set<Collection> getSupportedBy() {
        return this.published != null ? this.published.getSupportedBy() : this.versions.first().getSupportedBy();
    }

    public String getLocation() {
        return this.published != null ? this.published.getPage() : this.versions.first().getPage();
    }


    public Set<String> getAuthors() {
        return this.published != null ? this.published.getAuthors() : this.versions.first().getAuthors();
    }

    @Override
    public ServiceVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(net.metadata.dataspace.data.model.Version published) {
        this.published = (ServiceVersion) published;
    }

    @Override
    public net.metadata.dataspace.data.model.Version getWorkingCopy() {
        return this.versions.first();
    }

    public Set<Service> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Service> sameAs) {
        this.sameAs = sameAs;
    }

    public Set<Agent> getCreators() {
        return creators;
    }

    public void setCreators(Set<Agent> creators) {
        this.creators = creators;
    }
}
