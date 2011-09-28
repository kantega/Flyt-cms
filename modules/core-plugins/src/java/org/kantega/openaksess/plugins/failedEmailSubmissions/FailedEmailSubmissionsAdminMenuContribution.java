package org.kantega.openaksess.plugins.failedEmailSubmissions;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class FailedEmailSubmissionsAdminMenuContribution extends UIContributionAdapter {
    private final List<MenuItem> menuItems;

    @Autowired
    public FailedEmailSubmissionsAdminMenuContribution(UIServices uiServices) {
        MenuItem item = uiServices.createMenu();
        item.addLink("failedEmailSubmissions.title", "/oap/administration/failedEmailSubmissions");
        menuItems = Collections.singletonList(item);
    }

    @Override
    public List<MenuItem> getAdminMenuItems() {
        return menuItems;
    }
}
