package no.kantega.publishing.admin.menu;

import no.kantega.publishing.api.ui.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DefaultMenuItem implements MenuItem {
    private List<MenuItem> menuItems = new ArrayList<>();
    private String href;
    private String label;

    public DefaultMenuItem(String label) {
        this.label = label;
    }

    public DefaultMenuItem(String label, String href) {
        this.label = label;
        this.href = href;
    }

    public DefaultMenuItem() {
        
    }

    public String getHref() {
        return href;
    }

    public String getLabel() {
        return label;
    }

    public List<MenuItem> getChildMenuItems() {
        return menuItems;
    }

    public MenuItem addLink(String label, String href) {
        menuItems.add(new DefaultMenuItem(label, href));
        return this;
    }

    public MenuItem addChildMenuItem(String label) {
        final DefaultMenuItem item = new DefaultMenuItem(label);
        menuItems.add(item);
        return item;
    }


}
