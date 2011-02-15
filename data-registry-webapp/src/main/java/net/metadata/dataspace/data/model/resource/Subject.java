package net.metadata.dataspace.data.model.resource;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.record.AbstractRecordEntity;
import org.hibernate.validator.NotNull;

import javax.persistence.Entity;
import java.util.Date;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:14:18 PM
 */

@Entity
public class Subject extends AbstractRecordEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String vocabulary;

    @NotNull
    private String value;

    private String label;

    public Subject() {
    }

    public Subject(String vocabulary, String value) {
        this.vocabulary = vocabulary;
        this.value = value;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) obj;
        return getVocabulary().equals(other.getVocabulary()) && getValue().equals(other.getValue());
    }

    @Override
    public SortedSet<Version> getVersions() {
        return null;
    }

    @Override
    public Version getPublished() {
        return null;
    }

    @Override
    public void setPublished(Version version) {
    }

    @Override
    public Version getWorkingCopy() {
        return null;
    }

    @Override
    public void setUpdated(Date updated) {
        //TODO: we might need to implement this (i.e. add updated column)
    }

    @Override
    public Date getUpdated() {
        return null;
    }
}
