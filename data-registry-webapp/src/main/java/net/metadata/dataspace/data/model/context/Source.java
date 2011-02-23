package net.metadata.dataspace.data.model.context;

import org.hibernate.validator.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 10:40:38 AM
 */
@Entity
public class Source extends AbstractContextEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(unique = true)
    private String sourceURI;

    private String title;

    public Source() {
    }

    public Source(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
