package net.metadata.dataspace.data.model.version;

import net.metadata.dataspace.data.model.base.Activity;
import net.metadata.dataspace.data.model.base.Collection;
import net.metadata.dataspace.data.model.base.Party;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Collection> hasOutput = new HashSet<Collection>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Party> hasParticipant = new HashSet<Party>();

    public ActivityVersion() {
    }

    public Activity getParent() {
        return parent;
    }

    @Override
    public void setLocation(String text) {
    }

    public void setParent(Activity parent) {
        this.parent = parent;
    }

    public Set<Collection> getHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(Set<Collection> hasOutput) {
        this.hasOutput = hasOutput;
    }

    public Set<Party> getHasParticipant() {
        return hasParticipant;
    }

    public void setHasParticipant(Set<Party> hasParticipant) {
        this.hasParticipant = hasParticipant;
    }

}
