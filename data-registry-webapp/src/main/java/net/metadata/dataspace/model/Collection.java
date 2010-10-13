package net.metadata.dataspace.model;

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
    private String summary; //description

    @NotNull
    private Date updated;

    @CollectionOfElements
    private Set<String> authors;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<Subject>();

    @NotNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Party> collector = new HashSet<Party>();

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

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
