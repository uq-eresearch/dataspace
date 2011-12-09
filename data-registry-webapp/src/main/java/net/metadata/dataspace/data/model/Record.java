package net.metadata.dataspace.data.model;

import java.util.Calendar;
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
     * @return Unmodifiable sorted set (by created) of versions
     */
    SortedSet<V> getVersions();


    /**
     * Add a version to this record
     *
     * @param version
     * @return true if version was added, false if it was already present
     */
    boolean addVersion(V version);

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

    Calendar getUpdated();

    Calendar getCreated();

    String getUriKey();

    String getOriginalId();

    void setOriginalId(String originalId);

    Integer getAtomicNumber();

    void setAtomicNumber(Integer atomicNumber);

    boolean isActive();

    void setPublishDate(Calendar publishDate);

    Calendar getPublishDate();

    String getSourceRights();

    void setSourceRights(String rights);

    String getSourceLicense();

    void setSourceLicense(String license);
}
