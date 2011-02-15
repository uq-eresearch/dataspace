package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.resource.Subject;
import net.metadata.dataspace.data.model.types.CollectionType;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 02/11/2010
 * Time: 5:16:30 PM
 */
@Entity
public class CollectionVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Collection parent;

    @NotNull
    @Enumerated(STRING)
    private CollectionType type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Agent> collector = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Activity> isOutputOf = new HashSet<Activity>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Service> supports = new HashSet<Service>();

    @NotNull
    private String location; //URI

    public CollectionVersion() {
    }

    public Record getParent() {
        return parent;
    }

    public void setParent(Record parent) {
        this.parent = (Collection) parent;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Agent> getCollector() {
        return collector;
    }

    public void setCollector(Set<Agent> collector) {
        this.collector = collector;
    }

    public String getLocation() {
        return location;
    }

    public Set<Activity> getOutputOf() {
        return isOutputOf;
    }

    public void setOutputOf(Set<Activity> outputOf) {
        isOutputOf = outputOf;
    }

    public Set<Service> getSupports() {
        return supports;
    }

    public void setSupports(Set<Service> supports) {
        this.supports = supports;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CollectionType getType() {
        return type;
    }

    public void setType(CollectionType type) {
        this.type = type;
    }
}
