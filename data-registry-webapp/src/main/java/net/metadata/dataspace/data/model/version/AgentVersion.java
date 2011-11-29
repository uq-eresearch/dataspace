package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.AgentType;
import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 9:27:44 AM
 */
@Entity
public class AgentVersion extends AbstractVersionEntity<Agent> {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Agent parent;

    @NotNull
    @Enumerated(STRING)
    private AgentType type;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> mboxes = new HashSet<String>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_subjects")
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_made")
    private Set<Collection> made = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_is_manager_of")
    private Set<Collection> isManagerOf = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_managed_services")
    private Set<Service> managedServices = new HashSet<Service>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_current_projects")
    private Set<Activity> currentProjects = new HashSet<Activity>();


    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> alternatives = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> pages = new HashSet<String>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_publications")
    private Set<Publication> publications = new HashSet<Publication>();

    public AgentVersion() {
    }

    @Override
    public Agent getParent() {
        return parent;
    }

    @Override
    public void setParent(Agent parent) {
        this.parent = parent;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Collection> getIsManagerOf() {
        return isManagerOf;
    }

    public void setIsManagerOf(Set<Collection> isManagerOf) {
        this.isManagerOf = isManagerOf;
    }

    public Set<Service> getManagedServices() {
        return managedServices;
    }

    public void setManagedServices(Set<Service> managedServices) {
        this.managedServices = managedServices;
    }

    public Set<Activity> getCurrentProjects() {
        return currentProjects;
    }

    public void setCurrentProjects(Set<Activity> currentProjects) {
        this.currentProjects = currentProjects;
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
    }

    public Set<Collection> getMade() {
        return made;
    }

    public void setMade(Set<Collection> made) {
        this.made = made;
    }

    public Set<String> getMboxes() {
        return mboxes;
    }

    public void setMboxes(Set<String> mboxes) {
        this.mboxes = mboxes;
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

    public Set<Publication> getPublications() {
        return publications;
    }

    public void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }
}