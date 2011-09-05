package net.metadata.dataspace.data.model.context;

import javax.validation.constraints.NotNull;

import javax.persistence.Entity;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 10:38:58 AM
 */
@Entity
public class Publication extends AbstractContextEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String publicationURI;

    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
