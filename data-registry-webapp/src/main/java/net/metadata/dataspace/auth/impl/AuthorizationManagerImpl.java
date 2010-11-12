package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.auth.policy.AccessLevel;
import net.metadata.dataspace.data.model.base.Party;
import net.metadata.dataspace.data.model.base.Role;
import net.metadata.dataspace.data.model.base.User;
import net.metadata.dataspace.data.model.version.PartyVersion;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 12/11/2010
 * Time: 11:15:12 AM
 */
public class AuthorizationManagerImpl implements AuthorizationManager<User> {
    private static final AccessLevel EVERYTHING_ALLOWED = new AccessLevel(true, true, true, true);
    private static final Set<Class<?>> NORMAL_USERS_CAN_CREATE_INSTANCES = new HashSet<Class<?>>();

    static {
        NORMAL_USERS_CAN_CREATE_INSTANCES.add(Party.class);
        NORMAL_USERS_CAN_CREATE_INSTANCES.add(PartyVersion.class);
    }

    @Override
    public AccessLevel getAccessLevelForInstance(User user, Object instance) {
        if (user != null) {
            return EVERYTHING_ALLOWED;
        }
        boolean canCreate = false;
        boolean canRead = false;
        boolean canUpdate = false;
        boolean canDelete = false;

        if (instance instanceof Party) {
            canCreate = user != null; //logged in users can create Party
            canRead = true; //anyone can read Party
            canUpdate = user != null; //Users can edit their own profiles
            canDelete = user != null; // only super users can delete profiles
        }
        return new AccessLevel(canCreate, canRead, canUpdate, canDelete);
    }

    @Override
    public boolean canAccessWorkingCopy(User user, Class<?> clazz) {
        return false;
    }

    @Override
    public boolean canCreateInstances(User user, Class<?> clazz) {
        if (user == null) {
            return false;
        } else {
            if (user.getRole().equals(Role.ADMIN)) {
                return true;
            } else {
                return NORMAL_USERS_CAN_CREATE_INSTANCES.contains(clazz);
            }
        }
    }
}
