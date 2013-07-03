package org.kantega.openaksess.plugins.dbdiff;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class DbDiffAdminMenuContribution extends UIContributionAdapter {
    private final List<MenuItem> menuItems;

    @Autowired
    public DbDiffAdminMenuContribution(UIServices uiServices) {
        MenuItem item = uiServices.createMenu();
        item.addLink("dbdiff.title", "/admin/oap/administration/dbdiff");
        menuItems = Collections.singletonList(item);
    }

    @Override
    public List<MenuItem> getAdminMenuItems() {
        return menuItems;
    }
}
