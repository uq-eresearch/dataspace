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

    Record getParent();

    void setParent(Record parent);

    String getUriKey();

    String getTitle();

    String getSummary();

    String getContent();

    Set<String> getAuthors();

    void setLocation(String text);

    void setTitle(String title);

    void setSummary(String summary);

    void setContent(String content);

    void setUpdated(Date updated);

    void setAuthors(Set<String> authors);
}
