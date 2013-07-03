package org.kantega.openaksess.plugins.metrics.dao;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIServices;

import java.util.List;

public class DummyUIServices implements UIServices {
    @Override
    public MenuItem createMenu() {
        return new MenuItem() {
            @Override
            public String getHref() {
                return null;
            }

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public MenuItem addChildMenuItem(String label) {
                return null;
            }

            @Override
            public List<MenuItem> getChildMenuItems() {
                return null;
            }

            @Override
            public MenuItem addLink(String label, String href) {
                return null;
            }
        };
    }
}
