package net.metadata.dataspace.model;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.URI;

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

    private URI key;

    @NotNull
    private String name;

    @NotNull
    private String description;

    private Subject subject;

    @NotNull
    private URI isManagedBy;

    @NotNull
    private URI location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public URI getKey() {
        return key;
    }

    public void setKey(URI key) {
        this.key = key;
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

    public URI getManagedBy() {
        return isManagedBy;
    }

    public void setManagedBy(URI managedBy) {
        isManagedBy = managedBy;
    }

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }
}
