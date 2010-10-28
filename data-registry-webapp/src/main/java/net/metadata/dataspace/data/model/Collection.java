package net.metadata.dataspace.data.model;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:27 PM
 */
@Entity
public class Collection extends AbstractBaseEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String title; //name

    @NotNull
    @Column(length = 1024)
    private String summary; //description

    @NotNull
    @Column(length = 4096)
    private String content;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @CollectionOfElements
    private Set<String> authors = new HashSet<String>();

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

    public Collection() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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
