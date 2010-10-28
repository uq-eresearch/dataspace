package net.metadata.dataspace.data.model;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: alabri
 * Date: 27/10/2010
 * Time: 10:30:02 AM
 */
@Entity
public class Activity extends AbstractBaseEntity {

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
    private Set<Collection> hasOutput = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Party> hasParticipant = new HashSet<Party>();

    public Activity() {
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Set<Collection> getHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(Set<Collection> hasOutput) {
        this.hasOutput = hasOutput;
    }

    public Set<Party> getHasParticipant() {
        return hasParticipant;
    }

    public void setHasParticipant(Set<Party> hasParticipant) {
        this.hasParticipant = hasParticipant;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }
}
