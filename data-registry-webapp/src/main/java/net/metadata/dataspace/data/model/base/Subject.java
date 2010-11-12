package net.metadata.dataspace.data.model.base;

import net.metadata.dataspace.data.model.Version;
import org.hibernate.validator.NotNull;

import javax.persistence.Entity;
import java.util.SortedSet;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:14:18 PM
 */

@Entity
public class Subject extends AbstractBaseEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String vocabulary;

    @NotNull
    private String value;

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
}
