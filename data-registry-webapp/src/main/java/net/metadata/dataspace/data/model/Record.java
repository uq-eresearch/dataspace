package net.metadata.dataspace.data.model;

import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.Agent;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

/**
 * Author: alabri
 * Date: 03/11/2010
 * Time: 2:01:27 PM
 */
public interface Record {


    Long getId();

    /**
     * Gets a all versions of this record
     *
     * @return Sorted set (by updated) of versions
     */
    SortedSet getVersions();

    /**
     * Gets the published version of this record
     *
     * @return the published version this record
     */
    Version getPublished();

    void setPublished(Version version);

    /**
     * Return the working copy of this record
     *
     * @return the latest version of this record
     */
    Version getWorkingCopy();

    /**
     * Return the date when the entity last modified
     *
     * @param updated last updated date
     */
    void setUpdated(Date updated);

    Date getUpdated();

    Date getCreated();

    String getUriKey();

    Integer getAtomicNumber();

    void setAtomicNumber(Integer atomicNumber);

    boolean isActive();

    void setLocatedOn(Source source);

    void setSource(Source source);

    void setPublishDate(Date publishDate);

    Date getPublishDate();

    Set<Agent> getAuthors();

    void setAuthors(Set<Agent> authors);

    String getRights();

    void setRights(String rights);

    String getLicense();

    void setLicense(String license);
}
