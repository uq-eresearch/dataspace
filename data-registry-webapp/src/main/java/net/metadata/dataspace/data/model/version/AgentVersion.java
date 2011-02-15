package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Agent;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.base.resource.Subject;
import net.metadata.dataspace.data.model.types.AgentType;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 01/11/2010
 * Time: 9:27:44 AM
 */
@Entity
public class AgentVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Agent parent;

    @NotNull
    @Enumerated(STRING)
    private AgentType type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<Subject>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Collection> collectorOf = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Activity> isParticipantIn = new HashSet<Activity>();

    public AgentVersion() {
    }

    public Agent getParent() {
        return parent;
    }

    @Override
    public void setLocation(String text) {
    }

    public void setParent(Record parent) {
        this.parent = (Agent) parent;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Collection> getCollectorOf() {
        return collectorOf;
    }

    public void setCollectorOf(Set<Collection> collectorOf) {
        this.collectorOf = collectorOf;
    }

    public Set<Activity> getParticipantIn() {
        return isParticipantIn;
    }

    public void setParticipantIn(Set<Activity> participantIn) {
        isParticipantIn = participantIn;
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
    }
}