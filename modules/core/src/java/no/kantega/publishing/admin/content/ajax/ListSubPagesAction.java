/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.util.templates.AssociationCategoryHelper;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.model.MenuList;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class ListSubPagesAction implements Controller {
    private TemplateConfigurationCache templateConfigurationCache;
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);

        RequestParameters params = new RequestParameters(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        // Extracting currently selected content from it's url
        Content currentContent = null;
        if (!"".equals(url)) {
            ContentIdentifier cid = null;
            try {
                cid = new ContentIdentifier(request, url);
                currentContent = cms.getContent(cid);
            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();

        Map<Integer, MenuList> menus = new HashMap<Integer, MenuList>();

        if (currentContent != null) {
            // Find allowed associationcategories
            AssociationCategoryHelper helper = new AssociationCategoryHelper(templateConfigurationCache);
            ContentTemplate contentTemplate = cms.getContentTemplate(currentContent.getContentTemplateId());
            List<AssociationCategory> allowedAssociations = helper.getAllowedAssociationCategories(contentTemplate);
            for (AssociationCategory a : allowedAssociations) {
                MenuList menu = new MenuList();
                menu.setName(a.getName());
                menu.setId(a.getId());
                menus.put(a.getId(), menu);
            }

            // Find all subpages
            ContentQuery query = new ContentQuery();
            query.setAssociatedId(currentContent.getContentIdentifier());
            query.setShowExpired(true);
            List<Content> subPages = cms.getContentSummaryList(query, -1, new SortOrder(ContentProperty.PRIORITY, false));
            for (Content subPage : subPages) {
                int menuId = subPage.getAssociation().getCategory().getId();
                MenuList menu = getMenuList(cms, menus, menuId);
                menu.addSubPage(subPage);
            }


            model.put("menus", menus.values());
        }

        return new ModelAndView(view, model);
    }

    private MenuList getMenuList(ContentManagementService cms, Map<Integer, MenuList> menus, int menuId) {
        MenuList menu = menus.get(menuId);
        if (menu == null) {
            String name = "Untitled";
            // Page is placed in association category no longer available
            AssociationCategory category = cms.getAssociationCategory(menuId);
            if (category != null) {
                name = category.getName();
            }
            menu = new MenuList();
            menu.setName(name);
        }
        return menu;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }
}
