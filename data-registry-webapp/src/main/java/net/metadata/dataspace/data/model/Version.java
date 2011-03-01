package net.metadata.dataspace.data.model;

import java.util.Date;

/**
 * Author: alabri
 * Date: 03/11/2010
 * Time: 1:48:52 PM
 */
public interface Version {

    void setAtomicNumber(Integer atomicNumber);

    Integer getAtomicNumber();

    Date getUpdated();

    Date getCreated();

    Record getParent();

    void setParent(Record parent);

    String getUriKey();

    String getTitle();

    String getDescription();

    void setPage(String text);

    void setTitle(String title);

    void setDescription(String content);

    void setUpdated(Date updated);
}
