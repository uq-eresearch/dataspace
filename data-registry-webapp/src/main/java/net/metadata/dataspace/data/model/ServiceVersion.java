package net.metadata.dataspace.data.model;

import org.hibernate.validator.NotNull;

import javax.persistence.*;
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

}
