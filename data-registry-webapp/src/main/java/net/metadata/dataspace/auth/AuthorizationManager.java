package net.metadata.dataspace.auth;

import net.metadata.dataspace.auth.policy.AccessLevel;
import net.metadata.dataspace.data.model.record.User;

/**
 * Author: alabri
 * Date: 12/11/2010
 * Time: 10:16:02 AM
 * <p/>
 * Determines access to different resources.
 * <p/>
 * This interface is used to determine which access levels are granted to a
 * particular user for a particular resource. It is to be implemented in an
 * application specific manner.
 *
 * @param <U> The user to be used, usually an application specific class.
 */
public interface AuthorizationManager<U extends User> {

    /**
     * Determines the access level for a particular instance.
     * <p/>
     * The return value of this method determines what the user is allowed
     * to do with the given object. The semantics of the flags in the
     * {@link net.metadata.dataspace.auth.policy.AccessLevel} instance returned are:
     * <p/>
     * <ul>
     * <li>Create: the user is allowed to store this object as a new
     * entity in the database</li>
     * <li>Read: the user is allowed to read all contents of this
     * object</li>
     * <li>Update: the user can submit an updated version of the object
     * to the database</li>
     * <li>Delete: the user is allowed to delete the object from the
     * database</li>
     * </ul>
     *
     * @param user     The user whose access level is to be determined.
     * @param instance The object being accessed.
     * @return The access level the user has for the given object.
     */
    AccessLevel getAccessLevelForInstance(U user, Object instance);


    /**
     * Determines if the given user is allowed to working copies of objects.
     * <p/>
     * If and only if this method returns true, the given user is allowed access to a listing
     * of objects of the given class.
     * <p/>
     * Note that the detail of what is visible depends on the actual application. Access to the
     * concrete instance is determined by {@link #getAccessLevelForInstance(User, Object)}, some
     * applications might prefer to use only the latter to have more fine-grained control over the
     * content of listings.
     *
     * @param user  The user whose access level is to be determined.
     * @param clazz The object class being accessed.
     * @return True iff access is granted.
     */
    boolean canAccessWorkingCopy(U user, Class<?> clazz);

    /**
     * Determines if the given user is allowed to create objects of a certain type.
     * <p/>
     * This method should return true iff the user is allowed to create some kind of instances
     * of that class. If the user can create a particular instance is determined by
     * {@link #getAccessLevelForInstance(User, Object)}. The main purpose of this method is to
     * create dynamic user interfaces that won't allow access to object creation forms unless
     * there is at least some chance that the user can create an object.
     *
     * @param user  The user whose access level is to be determined.
     * @param clazz The object class being accessed.
     * @return True iff access is granted.
     */
    boolean canCreateInstances(U user, Class<?> clazz);
}