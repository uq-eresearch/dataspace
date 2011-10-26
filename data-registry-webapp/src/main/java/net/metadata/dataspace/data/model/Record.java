package net.metadata.dataspace.data.model;

import java.util.Date;
import java.util.SortedSet;

/**
 * Author: alabri
 * Date: 03/11/2010
 * Time: 2:01:27 PM
 */
public interface Record<V extends Version<?>> {


    Long getId();

    /**
     * Get the current title for this record
     *
     * @return Title string
     */
    String getTitle();

    /**
     * Get the current content (often the description) for this record
     *
     * @return Description string
     */
    String getContent();

    /**
     * Gets a all versions of this record
     *
     * @return Sorted set (by updated) of versions
     */
    SortedSet<V> getVersions();

    /**
     * Gets the published version of this record
     *
     * @return the published version this record
     */
    V getPublished();

    void setPublished(V version);

    /**
     * Return the working copy of this record
     *
     * @return the latest version of this record
     */
    V getWorkingCopy();

    /**
     * Return the date when the entity last modified
     *
     * @param updated last updated date
     */
    void setUpdated(Date updated);

    Date getUpdated();

    Date getCreated();

    String getUriKey();

    String getOriginalId();

    void setOriginalId(String originalId);

    Integer getAtomicNumber();

    void setAtomicNumber(Integer atomicNumber);

    boolean isActive();

    void setPublishDate(Date publishDate);

    Date getPublishDate();

    String getRights();

    void setRights(String rights);

    String getLicense();

    void setLicense(String license);
}
