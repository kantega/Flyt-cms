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

    private final List<MenuItem> adminMenuItems = new ArrayList<>();

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

    @SuppressWarnings("unchecked")
    private void processMenuItem(MenuItem item, Map<String, Object> map) {
        for(Map.Entry<String, Object> i: map.entrySet()) {
            Object dest = i.getValue();
            if(dest instanceof String) {
                item.addLink(i.getKey(), (String) dest);
            } else if (dest instanceof Map) {
                MenuItem subitem = item.addChildMenuItem(i.getKey());
                processMenuItem(subitem, (Map<String, Object>) dest);
            } else {
                throw new IllegalArgumentException("Map key " + i.getKey() +" maps to illegal type " + dest.getClass().getName());
            }
        }
    }
}
