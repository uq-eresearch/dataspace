package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.SourceAuthor;
import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
import net.metadata.dataspace.util.DaoHelper;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.NotNull;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 11:40:11 AM
 */
@MappedSuperclass
public abstract class AbstractVersionEntity<R extends Record<?>> implements Serializable, Comparable<Version<R>>, Version<R> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7632183952742865428L;

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

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    @NotNull
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

	@CollectionOfElements(fetch = FetchType.LAZY)
	private Set<SourceAuthor> descriptionAuthors = new HashSet<SourceAuthor>();

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Source source;

	@ManyToOne
	private R parent;

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
    public int compareTo(Version<R> version) {
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
        @SuppressWarnings("unchecked")
		AbstractVersionEntity<R> other = (AbstractVersionEntity<R>) obj;
        return getId().equals(other.getId());
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

	@Override
	public Set<SourceAuthor> getDescriptionAuthors() {
		return descriptionAuthors;
	}

	@Override
	public void setDescriptionAuthors(Set<SourceAuthor> descriptionAuthors) {
		this.descriptionAuthors = descriptionAuthors;
	}

	@Override
	public Source getSource() {
		return source;
	}

	@Override
	public void setSource(Source source) {
		this.source = source;
	}
	
    @Override
    public R getParent() {
		return parent;
    }

    @Override
    public void setParent(R parent) {
        this.parent = parent;
    }

}
