package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.ServiceType;
import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 02/11/2010
 * Time: 5:16:16 PM
 */
@Entity
public class ServiceVersion extends AbstractVersionEntity<Service> {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Service parent;

    @NotNull
    @Enumerated(STRING)
    private ServiceType type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "services_is_supported_by")
    private Set<Collection> isSupportedBy = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "services_managers")
    private Set<Agent> managedBy = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "services_subjects")
    private Set<Subject> subjects = new HashSet<Subject>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> alternatives = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> pages = new HashSet<String>();

    public ServiceVersion() {
    }

    @Override
    public Service getParent() {
        return parent;
    }

    @Override
    public void setParent(Service parent) {
        this.parent = parent;
    }

    public Set<Collection> getSupportedBy() {
        return isSupportedBy;
    }

    public void setSupportedBy(Set<Collection> supportedBy) {
        isSupportedBy = supportedBy;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public Set<Agent> getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(Set<Agent> managedBy) {
        this.managedBy = managedBy;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<String> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(Set<String> alternatives) {
        this.alternatives = alternatives;
    }

    public Set<String> getPages() {
        return pages;
    }

    public void setPages(Set<String> pages) {
        this.pages = pages;
    }
}
