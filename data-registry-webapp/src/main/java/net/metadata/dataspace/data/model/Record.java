package net.metadata.dataspace.data.model;

import java.util.SortedSet;

/**
 * Author: alabri
 * Date: 03/11/2010
 * Time: 2:01:27 PM
 */
public interface Record {

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

    /**
     * Return the working copy of this record
     *
     * @return the latest version of this record
     */
    Version getWorkingCopy();

}
