package no.kantega.publishing.ui;

import no.kantega.publishing.admin.menu.DefaultMenuItem;
import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIServices;

/**
 *
 */
public class DefaultUIServices implements UIServices {

    public MenuItem createMenu() {
        return new DefaultMenuItem();
    }
}
