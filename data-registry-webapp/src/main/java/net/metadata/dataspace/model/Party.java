package net.metadata.dataspace.model;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:39 PM
 */
@Entity
public class Party extends AbstractBaseEntity {

    //AtomPub related
    @NotNull
    private String title; //name

    private String summary; //description

    private Date updated;

    @CollectionOfElements
    private List<String> authors = new ArrayList<String>();

    //Other attributes
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<Subject>();

    private String collectorOfURI;

    public Party() {
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getCollectorOfURI() {
        return collectorOfURI;
    }

    public void setCollectorOfURI(String collectorOfURI) {
        this.collectorOfURI = collectorOfURI;
    }
}
