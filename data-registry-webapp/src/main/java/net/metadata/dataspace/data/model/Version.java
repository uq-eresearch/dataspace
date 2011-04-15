package net.metadata.dataspace.data.model;

import java.util.Date;
import java.util.Set;

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

    void setPages(Set<String> pages);

    Set<String> getAlternatives();

    void setAlternatives(Set<String> alternatives);

    Set<String> getPages();

    void setTitle(String title);

    void setDescription(String content);

    void setUpdated(Date updated);

    void setOriginalId(String originalId);

    String getOriginalId();
}
