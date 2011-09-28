package org.kantega.openaksess.plugins.failedSubmissions;


import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class FailedSubmissionsAdminMenuContribution extends UIContributionAdapter {
    private final List<MenuItem> menuItems;

    @Autowired
    public FailedSubmissionsAdminMenuContribution(UIServices uiServices) {
        MenuItem item = uiServices.createMenu();
        item.addLink("failedSubmissions.title", "/oap/administration/failedSubmissions");
        menuItems = Collections.singletonList(item);
    }

    @Override
    public List<MenuItem> getAdminMenuItems() {
        return menuItems;
    }
}
