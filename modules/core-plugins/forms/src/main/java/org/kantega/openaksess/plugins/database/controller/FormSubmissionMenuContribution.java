package org.kantega.openaksess.plugins.database.controller;

import no.kantega.publishing.api.ui.MenuItem;
import no.kantega.publishing.api.ui.UIContributionAdapter;
import no.kantega.publishing.api.ui.UIServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class FormSubmissionMenuContribution extends UIContributionAdapter {


        private final List<MenuItem> menuItems;

        @Autowired
        public FormSubmissionMenuContribution(UIServices uiServices) {
            MenuItem item = uiServices.createMenu();
            item.addLink("forms.title", "/admin/administration/submittedForms");
            menuItems = Collections.singletonList(item);
        }

        @Override
        public List<MenuItem> getAdminMenuItems() {
            return menuItems;
        }
    }



