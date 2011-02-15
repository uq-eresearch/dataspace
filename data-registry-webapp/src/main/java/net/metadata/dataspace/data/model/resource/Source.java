package net.metadata.dataspace.data.model.resource;

import org.hibernate.validator.NotNull;

import javax.persistence.Entity;

/**
 * Author: alabri
 * Date: 15/02/2011
 * Time: 10:40:38 AM
 */
@Entity
public class Source extends AbstractResourceEntity {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String sourceURI;

    @NotNull
    private String locatedOn;

    public Source() {
    }

    public Source(String sourceURI, String locatedOn) {
        this.sourceURI = sourceURI;
        this.locatedOn = locatedOn;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String getLocatedOn() {
        return locatedOn;
    }

    public void setLocatedOn(String locatedOn) {
        this.locatedOn = locatedOn;
    }
}
