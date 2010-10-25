package net.metadata.dataspace.data.model;

import net.metadata.dataspace.util.DaoHelper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 12:12:47 PM
 */
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private boolean isActive;

    public AbstractBaseEntity() {
        this.isActive = true;
    }

    public String getUriKey() {
        return DaoHelper.fromDecimalToOtherBase(31, getId().intValue());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
        return getId().equals(other.getId());
    }

}