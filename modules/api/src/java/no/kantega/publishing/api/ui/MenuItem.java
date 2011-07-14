package no.kantega.publishing.api.ui;

import java.util.List;

/**
 *
 */
public interface MenuItem {
    String getHref();
    String getLabel();
    MenuItem addChildMenuItem(String label);
    List<MenuItem> getChildMenuItems();
    MenuItem addLink(String label, String href);
}
