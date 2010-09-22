package net.metadata.dataspace.model;

import org.hibernate.validator.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:39 PM
 */
@Entity
public class Party extends AbstractBaseEntity {

    @NotNull
    private String keyURI;

    @NotNull
    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<Subject>();


    private String collectorOfURI;

    public Party() {
    }

    public String getKeyURI() {
        return keyURI;
    }

    public void setKeyURI(String keyURI) {
        this.keyURI = keyURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
