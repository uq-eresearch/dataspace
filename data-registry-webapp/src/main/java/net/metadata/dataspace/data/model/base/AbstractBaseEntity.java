package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.util.DaoHelper;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 12:12:47 PM
 */
@MappedSuperclass
public abstract class AbstractBaseEntity<T> implements Serializable, Record {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Integer atomicNumber;

    private boolean isActive;

    public AbstractBaseEntity() {
        this.isActive = true;
    }

    abstract public SortedSet<T> getVersions();

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
        if (!(obj instanceof AbstractBaseEntity)) {
            return false;
        }
        AbstractBaseEntity other = (AbstractBaseEntity) obj;
        return getId().equals(other.getId());
    }

}