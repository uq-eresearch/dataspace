package net.metadata.dataspace.auth.policy;

/**
 * Author: alabri
 * Date: 12/11/2010
 * Time: 10:17:45 AM
 */

/**
 * Model for the access to a particular resource.
 * <p/>
 * This models the standard CRUD (Create/Read/Update/Delete) view for access
 * control.
 */
public class AccessLevel {
    /**
     * Full access (create, read, update delete).
     */
    public static final AccessLevel FULL = new AccessLevel(true, true, true, true);

    /**
     * Read-only access.
     */
    public static final AccessLevel READ = new AccessLevel(false, true, false, false);

    /**
     * No access at all.
     */
    public static final AccessLevel NOTHING = new AccessLevel(false, false, false, false);

    private final boolean canCreate;
    private final boolean canRead;
    private final boolean canUpdate;
    private final boolean canDelete;

    /**
     * Construct a new instance with the given access control settings.
     *
     * @param canCreate Determines if creation of the referenced resource is allowed.
     * @param canRead   Determines if read access to the referenced resource is allowed.
     * @param canUpdate Determines if updating the referenced resource is allowed.
     * @param canDelete Determines if deleting the referenced resource is allowed.
     */
    public AccessLevel(boolean canCreate, boolean canRead, boolean canUpdate, boolean canDelete) {
        this.canCreate = canCreate;
        this.canRead = canRead;
        this.canUpdate = canUpdate;
        this.canDelete = canDelete;
    }

    public boolean canCreate() {
        return canCreate;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canUpdate() {
        return canUpdate;
    }
}
