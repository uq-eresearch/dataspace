package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.Version;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.version.AgentVersion;
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
 * Time: 3:32:39 PM
 */
@Entity
public class Agent extends AbstractRecordEntity<AgentVersion> {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    @NotNull
    @Sort(type = SortType.NATURAL)
    private SortedSet<AgentVersion> versions = new TreeSet<AgentVersion>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AgentVersion published;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agent_same_as")
    private Set<Agent> sameAs = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "agent_description_authors")
    private Set<Agent> authors = new HashSet<Agent>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source locatedOn;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Source source;

    public Agent() {
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

    public Set<Collection> getIsManagerOf() {
        return this.versions.first().getIsManagerOf();
    }

    public Set<Collection> getMade() {
        return this.versions.first().getMade();
    }

    public Set<Activity> getParticipantIn() {
        return this.versions.first().getCurrentProjects();
    }

    public Set<String> getMBoxes() {
        return this.versions.first().getMboxes();
    }

    public SortedSet<AgentVersion> getVersions() {
        return versions;
    }

    public void setVersions(SortedSet<AgentVersion> versions) {
        this.versions = versions;
    }

    @Override
    public AgentVersion getPublished() {
        return published;
    }

    @Override
    public void setPublished(Version version) {
        this.published = (AgentVersion) version;
    }

    @Override
    public AgentVersion getWorkingCopy() {
        return this.versions.first();
    }


    public Set<Agent> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<Agent> sameAs) {
        this.sameAs = sameAs;
    }

    @Override
    public void setAuthors(Set<Agent> authors) {
        this.authors = authors;
    }

    @Override
    public Set<Agent> getAuthors() {
        return this.authors;
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
