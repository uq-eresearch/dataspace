package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<CollectionVersion> versions = new TreeSet<CollectionVersion>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CollectionVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_same_as")
    private Set<Collection> sameAs = new HashSet<Collection>();


    public Collection() {

    }

    public SortedSet<CollectionVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<CollectionVersion> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return this.versions.first().getTitle();
    }

    public String getContent() {
        return this.versions.first().getDescription();
    }

    public Set<Subject> getSubjects() {
        return this.versions.first().getSubjects();
    }

    public Set<Agent> getCreators() {
        return this.versions.first().getCreators();
    }

    public Set<Agent> getPublishers() {
        return this.versions.first().getPublishers();
    }

    public Set<Activity> getOutputOf() {
        return this.versions.first().getOutputOf();
    }

    public Set<Service> getAccessedVia() {
        return this.versions.first().getAccessedVia();
    }

    public Set<Collection> getRelations() {
        return this.versions.first().getRelations();
    }

    @Override
    public CollectionVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(CollectionVersion published) {
        this.published = published;
    }

    @Override
    public CollectionVersion getWorkingCopy() {
        return this.versions.first();
    }

    public Set<Collection> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Collection> sameAs) {
        this.sameAs = sameAs;
    }
}
