package net.metadata.dataspace.model;

import org.hibernate.validator.NotNull;

import javax.persistence.*;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:27 PM
 */
@Entity
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String keyURI;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private Subject subject;

    @NotNull
    private String managedByURI;

    @NotNull
    private String locationURI;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
