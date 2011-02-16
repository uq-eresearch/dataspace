package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.context.Publication;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import net.metadata.dataspace.data.model.record.Service;
import net.metadata.dataspace.data.model.types.CollectionType;
import org.hibernate.validator.NotNull;

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
public class CollectionVersion extends AbstractVersionEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Collection parent;

    @NotNull
    @Enumerated(STRING)
    private CollectionType type;

    @NotNull
    private String page; //URI

    @NotNull
    @Column(length = 4096)
    private String rights;

    @Column(length = 4096)
    private String accessRights;

    private String license;

    private String temporal;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_agents_creators")
    private Set<Agent> creators = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_agent_publishers")
    private Set<Agent> publishers = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_activities_is_output_of")
    private Set<Activity> isOutputOf = new HashSet<Activity>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "collections_activities_is_accessed_via")
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


    public CollectionVersion() {
    }

    @Override
    public Record getParent() {
        return parent;
    }

    @Override
    public void setParent(Record parent) {
        this.parent = (Collection) parent;
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

    public String getPage() {
        return page;
    }

    @Override
    public void setPage(String page) {
        this.page = page;
    }

    public CollectionType getType() {
        return type;
    }

    public void setType(CollectionType type) {
        this.type = type;
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

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
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

    public String getTemporal() {
        return temporal;
    }

    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }
}
