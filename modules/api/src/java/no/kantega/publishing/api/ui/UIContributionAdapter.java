package no.kantega.publishing.api.ui;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class UIContributionAdapter implements UIContribution {

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
        return Collections.emptyList();
    }
}
