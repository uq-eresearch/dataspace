package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.record.AbstractBaseEntity;
import net.metadata.dataspace.util.DaoHelper;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 11:40:11 AM
 */
@MappedSuperclass
public abstract class AbstractVersionEntity implements Serializable, Comparable, net.metadata.dataspace.data.model.Version {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @JoinColumn(name = "parent")
    private Integer atomicNumber;

    @NotNull
    private String title; //name

    @NotNull
    @Column(length = 4096)
    private String description;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @CollectionOfElements
    private Set<String> authors = new HashSet<String>();

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    @Override
    public int compareTo(Object o) {

        net.metadata.dataspace.data.model.Version version = (net.metadata.dataspace.data.model.Version) o;
        if (this.getUpdated().equals(version.getUpdated())) {
            return 0;
        }
        if (this.getUpdated().before(version.getUpdated())) {
            return 1;
        }
        if (this.getUpdated().after(version.getUpdated())) {
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
