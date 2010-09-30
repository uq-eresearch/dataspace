package net.metadata.dataspace.model;

import org.hibernate.validator.NotNull;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 12:12:47 PM
 */
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {

    //Internal id used by hibernate
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //TODO this should be a unique URI
    @NotNull
    private String uriKey;

    public AbstractBaseEntity() {
        this.uriKey = UUID.randomUUID().toString();
    }

    public String getUriKey() {
        return uriKey;
    }

    public void setUriKey(String uriKey) {
        this.uriKey = uriKey;
    }

    @Override
    public int hashCode() {
        return uriKey.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AbstractBaseEntity)) {
            return false;
        }
        AbstractBaseEntity other = (AbstractBaseEntity) obj;
        return getId().equals(other.getId()) && getUriKey().equals(other.getUriKey());
    }
}