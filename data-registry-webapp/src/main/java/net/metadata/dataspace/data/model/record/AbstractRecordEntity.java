package net.metadata.dataspace.data.model.record;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.util.DaoHelper;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

/**
 * User: alabri
 * Date: 22/09/2010
 * Time: 12:12:47 PM
 */
@MappedSuperclass
public abstract class AbstractRecordEntity<V extends Version<?>> implements Serializable, Record<V> {

    /**
	 *
	 */
	private static final long serialVersionUID = 3739725990930325307L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    protected SortedSet<V> versions = new TreeSet<V>();

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
    public SortedSet<V> getVersions() {
        return versions;
    }

    protected void setVersions(SortedSet<V> versions) {
        this.versions = versions;
    }

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

    public String getSourceRights() {
        return rights;
    }

    public void setSourceRights(String rights) {
        this.rights = rights;
    }

    public String getSourceLicense() {
        return license;
    }

    public void setSourceLicense(String license) {
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
        AbstractRecordEntity<?> other = (AbstractRecordEntity<?>) obj;
        return getId().equals(other.getId());
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String toString() {
    	return String.format("%s (%s)", getTitle(), getUriKey());
    }

	protected V getMostRecentVersion() {
		return this.getVersions().first();
	}

	public V getWorkingCopy() {
		return getMostRecentVersion();
	}

	public String getTitle() {
	    return getMostRecentVersion().getTitle();
	}

	public String getContent() {
	    return getMostRecentVersion().getDescription();
	}

}