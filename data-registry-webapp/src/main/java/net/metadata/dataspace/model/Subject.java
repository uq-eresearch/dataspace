package net.metadata.dataspace.model;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.URI;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:14:18 PM
 */

@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private URI vocabulary;

    @NotNull
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public URI getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
