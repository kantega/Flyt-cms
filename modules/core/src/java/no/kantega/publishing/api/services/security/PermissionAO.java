package no.kantega.publishing.api.services.security;

import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.security.data.ObjectPermissionsOverview;
import no.kantega.publishing.security.data.Permission;

import java.util.List;

public interface PermissionAO {
    /**
     * @param object permissions are wanted for.
     * @return all permission defined on the given <code>BaseObject</code>.
     */
    List<Permission> getPermissions(BaseObject object);

    /**
     * @return Overview of all Permissions for the given <code>no.kantega.publishing.common.data.enums.ObjectType</code>
     * @see no.kantega.publishing.common.data.enums.ObjectType
     */
    List<ObjectPermissionsOverview> getPermissionsOverview(int objectType);

    /**
     * Set <code>Permission</code>s for a <code>BaseObject</code>
     * @param object - The <code>BaseObject</code> to set permissions for.
     * @param permissions - The permissions to set.
     */
    void setPermissions(BaseObject object, List<Permission> permissions);
}
