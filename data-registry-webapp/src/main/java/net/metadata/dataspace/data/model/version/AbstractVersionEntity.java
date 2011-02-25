package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
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

    private boolean isActive;

    @NotNull
    private String title; //name

    @NotNull
    @Column(length = 4096)
    private String description;

    private String alternative;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @CollectionOfElements
    private Set<String> authors = new HashSet<String>();

    public AbstractVersionEntity() {
        this.isActive = true;
        this.created = new Date();
        this.updated = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUriKey() {
        return DaoHelper.fromDecimalToOtherBase(31, getAtomicNumber());
    }

    @Override
    public Integer getAtomicNumber() {
        return atomicNumber;
    }

    @Override
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
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Set<String> getAuthors() {
        return authors;
    }

    @Override
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
        if (!(obj instanceof AbstractRecordEntity)) {
            return false;
        }
        AbstractVersionEntity other = (AbstractVersionEntity) obj;
        return getId().equals(other.getId());
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

}
