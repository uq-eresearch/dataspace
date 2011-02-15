package net.metadata.dataspace.data.model.context;

import org.hibernate.validator.NotNull;

import javax.persistence.Entity;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 10:38:58 AM
 */
@Entity
public class Publication extends AbstractResourceEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String publicationURI;

    private String description;

    public Publication() {
    }

    public Publication(String publicationURI) {
        this.publicationURI = publicationURI;
    }

    public String getPublicationURI() {
        return publicationURI;
    }

    public void setPublicationURI(String publicationURI) {
        this.publicationURI = publicationURI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
