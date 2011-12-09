package net.metadata.dataspace.data.model.record;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.metadata.dataspace.data.model.version.ServiceVersion;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "service_same_as")
    private Set<Service> sameAs = new HashSet<Service>();

    public Service() {
    }

    public Set<Collection> getSupportedBy() {
        return getMostRecentVersion().getSupportedBy();
    }

    public Set<Agent> getManagedBy() {
        return getMostRecentVersion().getManagedBy();
    }

    @Override
    public ServiceVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(ServiceVersion published) {
        this.published = published;
    }

    public Set<Service> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Service> sameAs) {
        this.sameAs = sameAs;
    }

}
