package net.metadata.dataspace.data.model;

import net.metadata.dataspace.util.DaoHelper;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 11:40:11 AM
 */
@MappedSuperclass
public abstract class AbstractVersionEntity implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Integer atomicNumber;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    public AbstractVersionEntity() {
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


    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public int compareTo(Object o) {

        PartyVersion partyVersion = (PartyVersion) o;
        if (this.getUpdated().equals(partyVersion.getUpdated())) {
            return 0;
        }
        if (this.getUpdated().before(partyVersion.getUpdated())) {
            return 1;
        }
        if (this.getUpdated().after(partyVersion.getUpdated())) {
            return -1;
        }
        return 0;
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
        AbstractVersionEntity other = (AbstractVersionEntity) obj;
        return getId().equals(other.getId());
    }

}
