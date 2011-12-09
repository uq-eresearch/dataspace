package net.metadata.dataspace.data.model;

import java.util.Calendar;
import java.util.Set;

import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.SourceAuthor;

/**
 * Author: alabri
 * Calendar: 03/11/2010
 * Time: 1:48:52 PM
 */
public interface Version<R extends Record<?>> {

    Integer getAtomicNumber();

    Calendar getUpdated();

    Calendar getCreated();

    R getParent();

    void setParent(R parent);

    String getUriKey();

    String getTitle();

    String getDescription();

    void setPages(Set<String> pages);

    Set<String> getAlternatives();

    void setAlternatives(Set<String> alternatives);

    Set<String> getPages();

    void setTitle(String title);

    void setDescription(String content);

	void setSource(Source source);

	Source getSource();

	void setType(String type) throws UnknownTypeException;

	void setDescriptionAuthors(Set<SourceAuthor> descriptionAuthors);

	Set<SourceAuthor> getDescriptionAuthors();

}
