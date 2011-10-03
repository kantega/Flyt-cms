/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.util.templates;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.InvalidTemplateReferenceException;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Dec 17, 2008
 * Time: 3:23:58 PM
 */
public class TemplateHelper {
    private final static String SOURCE = "aksess.TemplateHelper";


    /**
     * Get a list of templates which can be used based on site and parenttemplate
     * @param siteId - SiteId of parent page
     * @param parentContentTemplateId - Id of content template to parent page
     * @return - List of allowed Content and DisplayTemplates
     * @throws SystemException - System error
     * @throws InvalidTemplateReferenceException - Reference to invalid template
     */
    @SuppressWarnings("unchecked")
    public static List getAllowedChildTemplates(int siteId, int parentContentTemplateId) throws SystemException, InvalidTemplateReferenceException {
        List templates = new ArrayList();

        List<DisplayTemplate> displayTemplates = DisplayTemplateCache.getTemplates();
        for (DisplayTemplate dt : displayTemplates) {
            // Find the contenttemplate for this displaytemplate
            int ctId = dt.getContentTemplate().getId();
            ContentTemplate ct = ContentTemplateCache.getTemplateById(ctId);
            if (ct == null) {
                throw new InvalidTemplateReferenceException(ctId, SOURCE);
            }
            if (isAllowedChild(ct, parentContentTemplateId)) {
                // Check is template is allowed for site
                boolean allowedForSite = false;
                if (dt.getSites().size() == 0) {
                    allowedForSite = true;
                } else {
                    for (Site s : dt.getSites()) {
                        if (s.getId() == siteId) {
                            allowedForSite = true;
                            break;
                        }
                    }
                }
                if (allowedForSite) {
                    if (dt.allowMultipleUsages() || !isTemplateInUse(dt.getId())) {
                        templates.add(dt);
                    }
                }
            }
        }

        List<ContentTemplate> contentTemplates = ContentTemplateCache.getTemplates();
        for (ContentTemplate ct : contentTemplates) {
            if (isAllowedChild(ct, parentContentTemplateId) && ct.getContentType() != ContentType.PAGE) {
                if (contentTemplateDoesNotHaveDisplayTemplate(ct, displayTemplates)) {
                    templates.add(ct);
                }

            }
        }
        return templates;
    }

    private static boolean contentTemplateDoesNotHaveDisplayTemplate(ContentTemplate ct, List<DisplayTemplate> displayTemplates) {
        for (DisplayTemplate dt : displayTemplates) {
            if (dt.getContentTemplate().getId() == ct.getId()){
                return false;
            }
        }
        return true;
    }

    /**
     * Get a list of displaytemplates which the user can change between
     * @param content - Content
     * @param isAdmin - Is user administrator
     * @return - List
     * @throws SystemException - System error
     */
    public static List<DisplayTemplate> getAllowedDisplayTemplatesForChange(Content content, boolean isAdmin) throws SystemException {

        ContentTemplate contentTemplate = ContentTemplateCache.getTemplateById(content.getContentTemplateId());

        // Find all allowed contenttemplates
        List<ContentTemplate> allowedContentTemplates = new ArrayList<ContentTemplate>();

        List<ContentTemplate> contentTemplates = ContentTemplateCache.getTemplates();
        for (ContentTemplate ct : contentTemplates) {
            if (ct.getTemplateFile().equals(contentTemplate.getTemplateFile())) {
                allowedContentTemplates.add(ct);
            }
        }

        // Find displaytemplates
        List<DisplayTemplate> allowedDisplayTemplates = new ArrayList<DisplayTemplate>();

        List<DisplayTemplate> displayTemplates = DisplayTemplateCache.getTemplates();
        for (DisplayTemplate dt : displayTemplates) {
            for (ContentTemplate ct : allowedContentTemplates) {
                if (ct.getId() == dt.getContentTemplate().getId() || isAdmin) {
                    if (isAllowed(content, dt) && !isAdded(allowedDisplayTemplates, dt)) {
                        allowedDisplayTemplates.add(dt);
                    }
                }
            }
        }

        return allowedDisplayTemplates;
    }

    
    private static boolean isAllowed(Content content, DisplayTemplate dt) {
        boolean allowed = true;
        if (dt.getId() != content.getDisplayTemplateId()) {
            for (Site s : dt.getSites()) {
                // Check if template can be used in site which content is published in
                List associations = content.getAssociations();
                for (int i = 0; i < associations.size(); i++) {
                    Association association =  (Association)associations.get(i);
                    if (association.getSiteId() != s.getId()) {
                        allowed = false;
                        break;
                    }
                }
            }
            if (!dt.allowMultipleUsages()) {
                // Template can only be used once, check if used before
                if (isTemplateInUse(dt.getId())) {
                    allowed = false;
                }
            }
        }
        return allowed;
    }


    private static boolean isAdded(List<DisplayTemplate> templates, DisplayTemplate dt) {
        for (DisplayTemplate d : templates) {
            if (d.getId() == dt.getId()) {
                return true;
            }
        }
        return false;
    }


    private static boolean isTemplateInUse(int displayTemplateId) {
        JdbcTemplate jdbctemplate = new JdbcTemplate(dbConnectionFactory.getDataSource());

        int count = jdbctemplate.queryForInt("select count(ContentId) from content where DisplayTemplateId = ? and ContentId in (select ContentId from associations where IsDeleted = 0 or IsDeleted is null)", new Object[] {displayTemplateId});
        return count > 0;
    }

    private static boolean isAllowedChild(ContentTemplate ct, int parentContentTemplate) {
        List<ContentTemplate> parentTemplates = ct.getAllowedParentTemplates();
        if (parentTemplates != null) {
            for (ContentTemplate parent : parentTemplates) {
                if (parent.getId() == parentContentTemplate) {
                    return true;
                }
            }
        }
        return false;
    }


}
