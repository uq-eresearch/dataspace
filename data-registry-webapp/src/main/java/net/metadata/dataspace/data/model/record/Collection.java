package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<CollectionVersion> versions = new TreeSet<CollectionVersion>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CollectionVersion published;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_same_as")
    private Set<Collection> sameAs = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_description_authors")
    private Set<Agent> authors = new HashSet<Agent>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source locatedOn;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source source;

    public Collection() {

    }

    public SortedSet<CollectionVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<CollectionVersion> versions) {
        this.versions = versions;
    }

    public String getTitle() {
        return this.published != null ? this.published.getTitle() : this.versions.first().getTitle();
    }

    public String getContent() {
        return this.published != null ? this.published.getDescription() : this.versions.first().getDescription();
    }

    public Set<Subject> getSubjects() {
        return this.published != null ? this.published.getSubjects() : this.versions.first().getSubjects();
    }

    public Set<Agent> getCollector() {
        return this.published != null ? this.published.getCreators() : this.versions.first().getCreators();
    }

    public Set<Activity> getOutputOf() {
        return this.published != null ? this.published.getOutputOf() : this.versions.first().getOutputOf();
    }

    public Set<Service> getSupports() {
        return this.published != null ? this.published.getAccessedVia() : this.versions.first().getAccessedVia();
    }

    @Override
    public CollectionVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(Version published) {
        this.published = (CollectionVersion) published;
    }

    @Override
    public Version getWorkingCopy() {
        return this.versions.first();
    }

    public Set<Collection> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Collection> sameAs) {
        this.sameAs = sameAs;
    }

    @Override
    public Set<Agent> getAuthors() {
        return this.authors;
    }

    @Override
    public void setAuthors(Set<Agent> authors) {
        this.authors = authors;
    }

    public Source getLocatedOn() {
        return locatedOn;
    }

    @Override
    public void setLocatedOn(Source locatedOn) {
        this.locatedOn = locatedOn;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public void setSource(Source source) {
        this.source = source;
    }
}
