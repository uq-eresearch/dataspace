package net.metadata.dataspace.data.model.record;

import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Mbox;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.version.AgentVersion;
import javax.validation.constraints.NotNull;

import javax.persistence.*;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FullName fullName;

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

    public Set<Service> getManagedServices() {
        return this.versions.first().getManagedServices();
    }

    public Set<Collection> getMade() {
        return this.versions.first().getMade();
    }

    public Set<Activity> getParticipantIn() {
        return this.versions.first().getCurrentProjects();
    }

    public Set<Mbox> getMBoxes() {
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
    public void setPublished(AgentVersion version) {
        this.published = version;
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

    public FullName getFullName() {
        return fullName;
    }

    public void setFullName(FullName fullName) {
        this.fullName = fullName;
    }
}
