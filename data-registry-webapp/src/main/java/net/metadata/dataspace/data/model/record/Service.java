package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.version.ServiceVersion;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:30:19 AM
 */
@Entity
public class Service extends AbstractRecordEntity<ServiceVersion> {

    private static final long serialVersionUID = 1L;

    public enum Type {
        ANNOTATE,
        ASSEMBLE,
        CREATE,
        GENERATE,
        HARVEST,
        REPORT,
        SEARCH,
        SYNDICATE,
        TRANSFORM
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<ServiceVersion> versions = new TreeSet<ServiceVersion>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "service_same_as")
    private Set<Service> sameAs = new HashSet<Service>();

    public Service() {
    }

    public SortedSet<ServiceVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<ServiceVersion> versions) {
        this.versions = versions;
    }

    public Set<Collection> getSupportedBy() {
        return this.versions.first().getSupportedBy();
    }

    public Set<Agent> getManagedBy() {
        return versions.first().getManagedBy();
    }

    @Override
    public ServiceVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(ServiceVersion published) {
        this.published = published;
    }

    @Override
    public ServiceVersion getWorkingCopy() {
        return this.versions.first();
    }

    public Set<Service> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Service> sameAs) {
        this.sameAs = sameAs;
    }

}
