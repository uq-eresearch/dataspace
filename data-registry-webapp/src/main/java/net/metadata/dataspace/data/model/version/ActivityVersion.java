package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.Record;
import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Agent;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.types.ActivityType;
import org.hibernate.validator.NotNull;

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
public class ActivityVersion extends AbstractVersionEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Activity parent;

    @NotNull
    @Enumerated(STRING)
    private ActivityType type;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Collection> hasOutput = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Agent> hasParticipant = new HashSet<Agent>();

    public ActivityVersion() {
    }

    public Activity getParent() {
        return parent;
    }

    @Override
    public void setLocation(String text) {
    }

    public void setParent(Record parent) {
        this.parent = (Activity) parent;
    }

    public Set<Collection> getHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(Set<Collection> hasOutput) {
        this.hasOutput = hasOutput;
    }

    public Set<Agent> getHasParticipant() {
        return hasParticipant;
    }

    public void setHasParticipant(Set<Agent> hasParticipant) {
        this.hasParticipant = hasParticipant;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }
}
