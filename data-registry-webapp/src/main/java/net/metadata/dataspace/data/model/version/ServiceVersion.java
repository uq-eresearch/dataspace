package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.ServiceType;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

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
    @Enumerated(STRING)
    private ServiceType type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Collection> isSupportedBy = new HashSet<Collection>();

    @NotNull
    private String page; //URI

    public ServiceVersion() {
    }

    @Override
    public Service getParent() {
        return parent;
    }

    @Override
    public void setParent(Record parent) {
        this.parent = (Service) parent;
    }

    public Set<Collection> getSupportedBy() {
        return isSupportedBy;
    }

    public void setSupportedBy(Set<Collection> supportedBy) {
        isSupportedBy = supportedBy;
    }

    public String getPage() {
        return page;
    }

    @Override
    public void setPage(String page) {
        this.page = page;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }
}
