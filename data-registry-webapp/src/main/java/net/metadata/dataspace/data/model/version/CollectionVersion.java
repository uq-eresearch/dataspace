package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.UnknownTypeException;
import net.metadata.dataspace.data.model.context.Mbox;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Spatial;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import javax.validation.constraints.NotNull;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 02/11/2010
 * Time: 5:16:30 PM
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"atomicnumber","parent_id"}))
public class CollectionVersion extends AbstractVersionEntity<Collection> {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Enumerated(STRING)
    private Collection.Type type;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private Set<Mbox> mboxes = new HashSet<Mbox>();

    @NotNull
    @Column(length = 4096)
    private String rights;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "element", length = 4096)
    private Set<String> accessRights = new HashSet<String>();

    @Column(length = 4096)
    private String license;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> temporals = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> geoRssPoints = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "element", length = 4096)
    private Set<String> geoRssPolygons = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Spatial> spatialCoverage = new HashSet<Spatial>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> keywords = new HashSet<String>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_creators")
    private Set<Agent> creators = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_publishers")
    private Set<Agent> publishers = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_is_output_of")
    private Set<Activity> isOutputOf = new HashSet<Activity>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_is_accessed_via")
    private Set<Service> isAccessedVia = new HashSet<Service>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_subjects")
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_publications")
    private Set<Publication> isReferencedBy = new HashSet<Publication>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_relations")
    private Set<Collection> relations = new HashSet<Collection>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> alternatives = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> pages = new HashSet<String>();

    public CollectionVersion() {
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Agent> getCreators() {
        return creators;
    }

    public void setCreators(Set<Agent> creators) {
        this.creators = creators;
    }

    public Set<Activity> getOutputOf() {
        return isOutputOf;
    }

    public void setOutputOf(Set<Activity> outputOf) {
        isOutputOf = outputOf;
    }

    public Set<Service> getAccessedVia() {
        return isAccessedVia;
    }

    public void setAccessedVia(Set<Service> accessedVia) {
        this.isAccessedVia = accessedVia;
    }

    public Collection.Type getType() {
        return type;
    }

    public void setType(Collection.Type type) {
        this.type = type;
    }

    public void setType(String type) throws UnknownTypeException {
    	if (Collection.Type.valueOf(type) == null) {
    		throw new UnknownTypeException("Unknown type: "+type);
    	}
    	setType(Collection.Type.valueOf(type));
    }

    public Set<Mbox> getMboxes() {
        return mboxes;
    }

    public void setMboxes(Set<Mbox> mboxes) {
        this.mboxes = mboxes;
    }

    public Set<Agent> getPublishers() {
        return publishers;
    }

    public void setPublishers(Set<Agent> publishers) {
        this.publishers = publishers;
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

    public Set<Publication> getReferencedBy() {
        return isReferencedBy;
    }

    public void setReferencedBy(Set<Publication> referencedBy) {
        isReferencedBy = referencedBy;
    }

    public Set<Collection> getRelations() {
        return relations;
    }

    public void setRelations(Set<Collection> relations) {
        this.relations = relations;
    }

    public Set<String> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(Set<String> accessRights) {
        this.accessRights = accessRights;
    }

    public Set<String> getTemporals() {
        return temporals;
    }

    public void setTemporals(Set<String> temporals) {
        this.temporals = temporals;
    }

    public Set<String> getGeoRssPoints() {
        return geoRssPoints;
    }

    public void setGeoRssPoints(Set<String> geoRssPoints) {
        this.geoRssPoints = geoRssPoints;
    }

    public Set<String> getGeoRssPolygons() {
        return geoRssPolygons;
    }

    public void setGeoRssPolygons(Set<String> geoRssBoxes) {
        this.geoRssPolygons = geoRssBoxes;
    }

    public Set<Spatial> getSpatialCoverage() {
		return spatialCoverage;
	}

	public void setSpatialCoverage(Set<Spatial> spatialCoverage) {
		this.spatialCoverage = spatialCoverage;
	}

	public Set<String> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(Set<String> alternatives) {
        this.alternatives = alternatives;
    }

    public Set<String> getPages() {
        return pages;
    }

    public void setPages(Set<String> pages) {
        this.pages = pages;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }
}
