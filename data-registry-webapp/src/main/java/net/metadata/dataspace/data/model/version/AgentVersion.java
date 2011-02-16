package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.types.AgentType;
import org.hibernate.validator.NotNull;

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
public class AgentVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Agent parent;

    @NotNull
    @Enumerated(STRING)
    private AgentType type;

    @NotNull
    private String mbox;

    private String page; //URI

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_subjects")
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_collections_made")
    private Set<Collection> made = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_collections_is_manager_of")
    private Set<Collection> isManagerOf = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_activities_current_projects")
    private Set<Activity> currentProjects = new HashSet<Activity>();

    public AgentVersion() {
    }

    @Override
    public Agent getParent() {
        return parent;
    }

    @Override
    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    @Override
    public void setParent(Record parent) {
        this.parent = (Agent) parent;
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

    public String getMbox() {
        return mbox;
    }

    public void setMbox(String mbox) {
        this.mbox = mbox;
    }

}