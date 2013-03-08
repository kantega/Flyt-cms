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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.preferences.UserPreference;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import no.kantega.publishing.admin.util.NavigatorUtil;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigatorAction implements Controller {

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private UserPreferencesManager userPreferencesManager;
    public String view;

    /**
     * Called on every request to render the content menu, typically triggerd by ajax. 
     * This might be a request to expand or collapse a menu element or a click that opens a new page in the contentmain frame.
     *
     * Retruns a view of the menu as it should look after the click.
     *
     * @param request - current request
     * @param response - current response
     * @return ModelAndView - Menu view
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Users should be able to navigate to all pages, must act as admin
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

        RequestParameters params = new RequestParameters(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        //Extracting currently selected content from it's url
        Content currentContent = null;
        int currentId = -1;
        String path = null;
        if (!"".equals(url)) {

            ContentIdentifier cid = null;
            try {
                cid = ContentIdHelper.fromRequestAndUrl(request, url);
                currentContent = cms.getContent(cid);
            } catch (ContentNotFoundException e) {
                // Do nothing
            }
            if (currentContent != null) {
                currentId = currentContent.getAssociation().getId();
                path = currentContent.getAssociation().getPath();
            }
        }

        int startId = params.getInt(AdminRequestParameters.START_ID);

        String openFoldersList = params.getString(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS);
        boolean expand = params.getBoolean(AdminRequestParameters.EXPAND, true);

        if (openFoldersList == null || openFoldersList.length() == 0) {
            openFoldersList = "0";
            if (currentContent == null) {
                // No folders open and no page is current, set startpage as open                
                try {
                    ContentIdentifier cid = ContentIdHelper.fromRequestAndUrl(request, "/");
                    currentId = cid.getAssociationId();
                } catch (ContentNotFoundException e) {
                    // Do nothing
                }
            }
            if (startId != -1) {
                ContentIdentifier cid =  ContentIdentifier.fromAssociationId(startId);
                if (!openFoldersList.contains(Integer.toString(startId))) {
                    try {
                        Content startContent = cms.getContent(cid);
                        openFoldersList += startContent.getAssociation().getPath().replaceAll("/", ",") + startId;
                    } catch (Exception e) {
                        Log.error(getClass().getSimpleName(), e, null, null);
                    }
                }
            }
        }

        openFoldersList = NavigatorUtil.getOpenFolders(expand, openFoldersList, path, currentId);

        String sort = getSort(request);

        int[] categoryList = null;
        String categories = params.getString(AdminRequestParameters.NAVIGATION_CATEGORIES);
        if (categories != null) {
            categoryList = StringHelper.getInts(categories, ",");
        }

        int[] openIds = StringHelper.getInts(openFoldersList, ",");
        List<SiteMapEntry> sites = new ArrayList<SiteMapEntry>();
        for (Site site : siteCache.getSites()) {
            if (!site.isDisabled() && !isHiddenByUser(site.getId(), request)) {
                SiteMapEntry sitemap = cms.getNavigatorMenu(site.getId(), openIds, sort, isShowExpired(request), categoryList);
                if (sitemap != null) {
                    sitemap.setTitle(site.getName());
                    sites.add(sitemap);
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put(AdminRequestParameters.NAVIGATION_SITES, sites);
        model.put(AdminRequestParameters.NAVIGATION_SORT_ORDER, sort);
        model.put(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS, openFoldersList);
        model.put(AdminRequestParameters.THIS_ID, currentId);
        model.put(AdminRequestParameters.START_ID, startId);
        model.put(AdminRequestParameters.HIGHLIGHT_CURRENT, params.getBoolean(AdminRequestParameters.HIGHLIGHT_CURRENT, true));

        return new ModelAndView(view, model);
    }


    
    /**
     * Determining the menu sort order
     *
     * @param request - The current HttpServletRequest
     * @return - String representing the sort order, e.g. ContentProperty.PRIORITY.
     */
    private String getSort(HttpServletRequest request) {
        String sort = request.getParameter(AdminRequestParameters.NAVIGATION_SORT_ORDER);

        UserPreference sortPreference = userPreferencesManager.getPreference(UserPreference.FILTER_SORT, request);
        if (sortPreference != null && sortPreference.getValue() != null && sortPreference.getValue().length() > 0) {
            sort = sortPreference.getValue();
        }

        if (sort == null) {
            sort = (String)request.getSession().getAttribute(AdminSessionAttributes.NAVIGATION_SORT_ORDER);
            if (sort == null) {
                sort = ContentProperty.PRIORITY;
            }
        }
        request.getSession().setAttribute(AdminSessionAttributes.NAVIGATION_SORT_ORDER, sort);

        return sort;
    }


    /**
     * Determines whether to show or hide expired content.
     *
     * @param request - The current HttpServletRequest
     * @return true if the user wants to show expired, otherwise false.
     */
    private boolean isShowExpired(HttpServletRequest request) {
        boolean showExpired = new RequestParameters(request).getBoolean(AdminRequestParameters.SHOW_EXPIRED, true);
        UserPreference hideExpiredPreference = userPreferencesManager.getPreference(UserPreference.FILTER_HIDE_EXPIRED, request);

        if (hideExpiredPreference == null || hideExpiredPreference.getValue() == null || hideExpiredPreference.getValue().length() == 0) {
            return showExpired;
        }
        if (Boolean.valueOf(hideExpiredPreference.getValue())) {
            return false;
        }
        return true;
    }


    /**

     *
     * @param id - The id of the site to check.
     * @param request - The current HttpServletRequest
     * @return true if the user wants to hide the site, otherwise false.
     */
    private boolean isHiddenByUser(int id, HttpServletRequest request) {
        UserPreference sitesPreference = userPreferencesManager.getPreference(UserPreference.FILTER_SITES, request);
        if (sitesPreference == null || sitesPreference.getValue() == null || sitesPreference.getValue().length() == 0) {
            return false;
        }
        String[] sites = sitesPreference.getValue().split(",");
        for (String site : sites) {
            try {
                int siteId = Integer.parseInt(site);
                if (siteId == id) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
    

    public void setView(String view) {
        this.view = view;
    }
}
