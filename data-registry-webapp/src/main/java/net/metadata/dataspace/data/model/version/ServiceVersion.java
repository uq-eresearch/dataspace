package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.UnknownTypeException;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
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
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"atomicnumber","parent_id"}))
public class ServiceVersion extends AbstractVersionEntity<Service> {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Enumerated(STRING)
    private Service.Type type;

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

    public Set<Collection> getSupportedBy() {
        return isSupportedBy;
    }

    public void setSupportedBy(Set<Collection> supportedBy) {
        isSupportedBy = supportedBy;
    }

    public Service.Type getType() {
        return type;
    }

    public void setType(Service.Type type) {
        this.type = type;
    }

    public void setType(String type) throws UnknownTypeException {
    	if (Service.Type.valueOf(type) == null) {
    		throw new UnknownTypeException("Unknown type: "+type);
    	}
    	setType(Service.Type.valueOf(type));
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
