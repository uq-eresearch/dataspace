package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.auth.AuthorizationManager;
import net.metadata.dataspace.auth.policy.AccessLevel;
import net.metadata.dataspace.data.model.base.*;
import net.metadata.dataspace.data.model.types.Role;
import net.metadata.dataspace.data.model.version.ActivityVersion;
import net.metadata.dataspace.data.model.version.CollectionVersion;
import net.metadata.dataspace.data.model.version.PartyVersion;
import net.metadata.dataspace.data.model.version.ServiceVersion;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: alabri
 * Date: 12/11/2010
 * Time: 11:15:12 AM
 */
public class AuthorizationManagerImpl implements AuthorizationManager<User> {
    private static final AccessLevel EVERYTHING_ALLOWED = new AccessLevel(true, true, true, true);
    private static final Set<Class<?>> LOGGED_IN_USERS_CAN_CREATE_INSTANCES = new HashSet<Class<?>>();

    static {
        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(Activity.class);
        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(ActivityVersion.class);

        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(Collection.class);
        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(CollectionVersion.class);

        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(Party.class);
        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(PartyVersion.class);

        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(Service.class);
        LOGGED_IN_USERS_CAN_CREATE_INSTANCES.add(ServiceVersion.class);
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
        return user != null;
    }

    @Override
    public boolean canCreateInstances(User user, Class<?> clazz) {
        if (user == null) {
            return false;
        } else {
            if (user.getRole().equals(Role.ADMIN)) {
                return true;
            } else {
                return LOGGED_IN_USERS_CAN_CREATE_INSTANCES.contains(clazz);
            }
        }
    }
}
