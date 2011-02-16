package net.metadata.dataspace.data.model.context;

import javax.persistence.Entity;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 4:14:18 PM
 */

@Entity
public class Subject extends AbstractContextEntity {

    private static final long serialVersionUID = 1L;

    private String term;

    private String isDefinedBy;

    private String label;

    public Subject() {
    }

    public Subject(String term, String isDefinedBy) {
        this.term = term;
        this.isDefinedBy = isDefinedBy;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinedBy() {
        return isDefinedBy;
    }

    public void setDefinedBy(String definedBy) {
        this.isDefinedBy = definedBy;
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
        return getTerm().equals(other.getTerm()) && getDefinedBy().equals(other.getDefinedBy());
    }

}
