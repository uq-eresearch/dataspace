package net.metadata.dataspace.data.model.context;

import javax.persistence.Entity;

/**
 * Author: alabri
 * Date: 14/04/11
 * Time: 10:31 AM
 */
@Entity
public class FullName extends AbstractContextEntity {
    private static final long serialVersionUID = 1L;

    private String title;
    private String givenName;
    private String familyName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
