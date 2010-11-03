package net.metadata.dataspace.data.model;

import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Party> collector = new HashSet<Party>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Activity> isOutputOf = new HashSet<Activity>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Service> supports = new HashSet<Service>();

    @NotNull
    private String location; //URI

    public CollectionVersion() {
    }

    public Collection getParent() {
        return parent;
    }

    public void setParent(Collection parent) {
        this.parent = parent;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Party> getCollector() {
        return collector;
    }

    public void setCollector(Set<Party> collector) {
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

}
