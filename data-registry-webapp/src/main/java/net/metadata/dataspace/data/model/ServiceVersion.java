package net.metadata.dataspace.data.model;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 02/11/2010
 * Time: 5:16:16 PM
 */
@Entity
public class ServiceVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Service parent;

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
    private Set<Collection> isSupportedBy = new HashSet<Collection>();

    @NotNull
    private String location; //URI

    public ServiceVersion() {
    }

    public Service getParent() {
        return parent;
    }

    public void setParent(Service parent) {
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Set<Collection> getSupportedBy() {
        return isSupportedBy;
    }

    public void setSupportedBy(Set<Collection> supportedBy) {
        isSupportedBy = supportedBy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }
}
