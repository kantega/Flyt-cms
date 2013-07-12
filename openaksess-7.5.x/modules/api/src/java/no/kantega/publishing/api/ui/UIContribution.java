package no.kantega.publishing.api.ui;

import java.util.List;

/**
 *
 */
public interface UIContribution {

    /**
     * Menu items destined for the OA top level menu, i.e. alongside <em>My page</em>, <em>Publish</em>,
     * <em>Media archive</em>, etc.
     * @return List of root menu items
     */
    List<MenuItem> getRootMenuItems();

    /**
     * Contributions to the OA administration page menu.
     * @return List of admin menu items
     */
    List<MenuItem> getAdminMenuItems();
}
