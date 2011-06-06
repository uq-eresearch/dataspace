package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.util.DaoHelper;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 12:12:47 PM
 */
@MappedSuperclass
public abstract class AbstractRecordEntity<V> implements Serializable, Record {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Integer atomicNumber;

    private boolean isActive;

    private String originalId;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date publishDate;

    private String rights;

    private String license;

    public AbstractRecordEntity() {
        this.isActive = true;
        this.created = new Date();
        this.updated = new Date();
    }

    @Override
    abstract public SortedSet<V> getVersions();

    @Override
    abstract public User getDescriptionAuthor();

    @Override
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

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
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
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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
        AbstractRecordEntity other = (AbstractRecordEntity) obj;
        return getId().equals(other.getId());
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}