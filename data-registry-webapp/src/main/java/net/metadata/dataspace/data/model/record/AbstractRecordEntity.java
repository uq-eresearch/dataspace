package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.context.Source;
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

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date publishDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source locatedOn;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source source;

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


    public Source getLocatedOn() {
        return locatedOn;
    }

    public void setLocatedOn(Source locatedOn) {
        this.locatedOn = locatedOn;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
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