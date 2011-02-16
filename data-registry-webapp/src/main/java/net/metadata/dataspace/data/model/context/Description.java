package net.metadata.dataspace.data.model.context;

import net.metadata.dataspace.data.model.record.Agent;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 16/02/2011
 * Time: 4:11:42 PM
 */
@Entity
public class Description extends AbstractContextEntity {

    private static final long serialVersionUID = 1L;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agents_agents_creators")
    private Set<Agent> creators = new HashSet<Agent>();

    @NotNull
    @ManyToOne
    private Agent publisher;

    @NotNull
    @ManyToOne
    private Source locatedOn;

    @NotNull
    @ManyToOne
    private Source source;

    private String rights;

    private String license;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "description_see_also")
    private Set<Description> seeAlso = new HashSet<Description>();

    public Description() {
    }

    public Agent getPublisher() {
        return publisher;
    }

    public void setPublisher(Agent publisher) {
        this.publisher = publisher;
    }

    public Set<Agent> getCreators() {
        return creators;
    }

    public void setCreators(Set<Agent> creators) {
        this.creators = creators;
    }

    public Source getLocatedOn() {
        return locatedOn;
    }

    public void setLocatedOn(Source locatedOn) {
        this.locatedOn = locatedOn;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Set<Description> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(Set<Description> seeAlso) {
        this.seeAlso = seeAlso;
    }
}
