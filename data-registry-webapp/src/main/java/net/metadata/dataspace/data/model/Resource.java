package net.metadata.dataspace.data.model;

import java.util.Date;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 11:46:18 AM
 */
public interface Resource {
    Long getId();

    String getUriKey();

    Integer getAtomicNumber();

    void setAtomicNumber(Integer atomicNumber);

    boolean isActive();

    Date getCreated();
}
