package org.kantega.openaksess.plugins.metrics;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class MetricsAdminMenuContribution extends UIContributionAdapter {
    private final List<MenuItem> menuItems;

    @Autowired
    public MetricsAdminMenuContribution(UIServices uiServices) {
        MenuItem item = uiServices.createMenu();
        item.addLink("metrics.title", "/admin/oap/administration/metrics");
        menuItems = Collections.singletonList(item);
    }

    @Override
    public List<MenuItem> getAdminMenuItems() {
        return menuItems;
    }
}
