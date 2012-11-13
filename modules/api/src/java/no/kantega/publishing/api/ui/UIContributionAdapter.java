package no.kantega.publishing.api.ui;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class UIContributionAdapter implements UIContribution {

    private final List<MenuItem> adminMenuItems = new ArrayList<MenuItem>();

    @Autowired
    private UIServices uiServices;

    /**
     * {@inheritDoc}
     */
    public List<MenuItem> getRootMenuItems() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<MenuItem> getAdminMenuItems() {
        return adminMenuItems;
    }

    public void setAdminMenuLinkMap(Map<String, Object> adminMenuLinks) {
        MenuItem item = uiServices.createMenu();

        processMenuItem(item, adminMenuLinks);
        adminMenuItems.add(item);
    }

    private void processMenuItem(MenuItem item, Map<String, Object> map) {
        for(String key : map.keySet()) {
            Object dest = map.get(key);
            if(dest instanceof String) {
                item.addLink(key, (String)dest);
            } else if (dest instanceof Map) {
                MenuItem subitem = item.addChildMenuItem(key);
                processMenuItem(subitem, (Map<String, Object>) dest);
            } else {
                throw new IllegalArgumentException("Map key " +key +" maps to illegal type " + dest.getClass().getName());
            }
        }
    }
}
