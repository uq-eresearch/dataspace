package net.metadata.dataspace.model;

import org.hibernate.validator.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:27 PM
 */
@Entity
public class Collection extends AbstractBaseEntity {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subject> subjects = new ArrayList<Subject>();

    @NotNull
    private String managedByURI;

    @NotNull
    private String locationURI;

    public Collection() {
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

    public String getManagedByURI() {
        return managedByURI;
    }

    public void setManagedByURI(String managedByURI) {
        this.managedByURI = managedByURI;
    }

    public String getLocationURI() {
        return locationURI;
    }

    public void setLocationURI(String locationURI) {
        this.locationURI = locationURI;
    }
}
