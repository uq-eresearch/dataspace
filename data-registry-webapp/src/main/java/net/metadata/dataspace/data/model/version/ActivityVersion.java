package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.UnknownTypeException;
import net.metadata.dataspace.data.model.context.Subject;
import net.metadata.dataspace.data.model.record.Activity;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.Collection;
import javax.validation.constraints.NotNull;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 02/11/2010
 * Time: 5:16:43 PM
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"atomicnumber","parent_id"}))
public class ActivityVersion extends AbstractVersionEntity<Activity> {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Enumerated(STRING)
    private Activity.Type type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activities_has_output")
    private Set<Collection> hasOutput = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activities_has_participants")
    private Set<Agent> hasParticipants = new HashSet<Agent>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "activities_subjects")
    private Set<Subject> subjects = new HashSet<Subject>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> alternatives = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> pages = new HashSet<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> temporals = new HashSet<String>();

    public ActivityVersion() {
    }

    public Set<Collection> getHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(Set<Collection> hasOutput) {
        this.hasOutput = hasOutput;
    }

    public Set<Agent> getHasParticipants() {
        return hasParticipants;
    }

    public void setHasParticipants(Set<Agent> hasParticipants) {
        this.hasParticipants = hasParticipants;
    }

    public Activity.Type getType() {
        return type;
    }

    public void setType(Activity.Type type) {
        this.type = type;
    }

    public void setType(String type) throws UnknownTypeException {
    	if (Activity.Type.valueOf(type) == null) {
    		throw new UnknownTypeException("Unknown type: "+type);
    	}
    	setType(Activity.Type.valueOf(type));
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
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

    public Set<String> getTemporals() {
        return temporals;
    }

    public void setTemporals(Set<String> temporals) {
        this.temporals = temporals;
    }
}
