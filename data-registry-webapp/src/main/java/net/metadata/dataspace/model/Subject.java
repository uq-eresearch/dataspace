package net.metadata.dataspace.model;

import org.hibernate.validator.NotNull;

import javax.persistence.Entity;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:14:18 PM
 */

@Entity
public class Subject extends AbstractBaseEntity {

    @NotNull
    private String vocabularyURI;

    @NotNull
    private String value;

    public Subject() {
    }

    public String getVocabularyURI() {
        return vocabularyURI;
    }

    public void setVocabularyURI(String vocabularyURI) {
        this.vocabularyURI = vocabularyURI;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
