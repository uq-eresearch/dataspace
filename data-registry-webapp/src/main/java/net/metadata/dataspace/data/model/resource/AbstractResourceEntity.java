package net.metadata.dataspace.data.model.resource;

import net.metadata.dataspace.data.model.Resource;
import net.metadata.dataspace.util.DaoHelper;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 11:45:18 AM
 */
@MappedSuperclass
public abstract class AbstractResourceEntity implements Serializable, Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Integer atomicNumber;

    private boolean isActive;

    public AbstractResourceEntity() {
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUriKey() {
        return DaoHelper.fromDecimalToOtherBase(31, getAtomicNumber());
    }

    public Integer getAtomicNumber() {
        return atomicNumber;
    }

    public void setAtomicNumber(Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
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
        if (!(obj instanceof AbstractResourceEntity)) {
            return false;
        }
        AbstractResourceEntity other = (AbstractResourceEntity) obj;
        return getId().equals(other.getId());
    }

}
