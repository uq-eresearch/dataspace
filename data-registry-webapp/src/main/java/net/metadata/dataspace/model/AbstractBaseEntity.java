package net.metadata.dataspace.model;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public AbstractBaseEntity() {
    }

    public String getUriKey() {
        return DaoHelper.fromDecimalToOtherBase(31, getId().intValue());
    }

    @Override
    public int hashCode() {
        return getUriKey().hashCode();
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
        return getId().equals(other.getId());
    }

}