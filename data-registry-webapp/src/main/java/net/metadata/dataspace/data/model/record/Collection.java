package net.metadata.dataspace.data.model.record;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.version.CollectionVersion;

/**
 * User: alabri
 * Date: 15/09/2010
 * Time: 3:32:27 PM
 */
@Entity
public class Collection extends AbstractRecordEntity<CollectionVersion> {

    private static final long serialVersionUID = 1L;

    public enum Type {
        COLLECTION,
        DATASET
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CollectionVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_same_as")
    private Set<Collection> sameAs = new HashSet<Collection>();


    public Collection() {

    }

    public Set<Subject> getSubjects() {
        return getMostRecentVersion().getSubjects();
    }

    public Set<Agent> getCreators() {
        return getMostRecentVersion().getCreators();
    }

    public Set<Agent> getPublishers() {
        return getMostRecentVersion().getPublishers();
    }

    public Set<Activity> getOutputOf() {
        return getMostRecentVersion().getOutputOf();
    }

    public Set<Service> getAccessedVia() {
        return getMostRecentVersion().getAccessedVia();
    }

    public Set<Collection> getRelations() {
        return getMostRecentVersion().getRelations();
    }

    @Override
    public CollectionVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(CollectionVersion published) {
        this.published = published;
    }

    public Set<Collection> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Collection> sameAs) {
        this.sameAs = sameAs;
    }
}
