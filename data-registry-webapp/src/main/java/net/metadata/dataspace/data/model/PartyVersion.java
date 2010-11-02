package net.metadata.dataspace.data.model;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 9:27:44 AM
 */
@Entity
public class PartyVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Party parent;

    @NotNull
    private String title; //name

    @NotNull
    @Column(length = 1024)
    private String summary; //description

    @NotNull
    @Column(length = 4096)
    private String content;

    @CollectionOfElements
    private Set<String> authors = new HashSet<String>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Collection> collectorOf = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Activity> isParticipantIn = new HashSet<Activity>();

    public PartyVersion() {
    }

    public Party getParent() {
        return parent;
    }

    public void setParent(Party parent) {
        this.parent = parent;
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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Collection> getCollectorOf() {
        return collectorOf;
    }

    public void setCollectorOf(Set<Collection> collectorOf) {
        this.collectorOf = collectorOf;
    }

    public Set<Activity> getParticipantIn() {
        return isParticipantIn;
    }

    public void setParticipantIn(Set<Activity> participantIn) {
        isParticipantIn = participantIn;
    }
}