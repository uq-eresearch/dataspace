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

}
