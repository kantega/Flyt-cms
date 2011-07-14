package org.kantega.openaksess.plugins.jobexecuter;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class JobExecuterAdminMenuContribution extends UIContributionAdapter {
    private final List<MenuItem> menuItems;

    @Autowired
    public JobExecuterAdminMenuContribution(UIServices uiServices) {
        MenuItem item = uiServices.createMenu();
        item.addLink("jobexecuter.title", "/admin/oap/administration/jobs");
        menuItems = Collections.singletonList(item);
    }

    @Override
    public List<MenuItem> getAdminMenuItems() {
        return menuItems;
    }
}
