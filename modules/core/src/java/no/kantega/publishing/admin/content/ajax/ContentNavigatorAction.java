/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import javax.servlet.http.HttpSession;

import no.kantega.publishing.common.service.SiteManagementService;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.util.StringHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 03.jul.2009
 * Time: 14:13:45
 */
public class ContentNavigatorAction implements Controller {

    public String viewName;

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

        HttpSession session = request.getSession();
        SiteManagementService siteService = new SiteManagementService(request);
        ContentManagementService cms = new ContentManagementService(request);

        RequestParameters params = new RequestParameters(request);

        String sort = params.getString(AdminRequestParameters.NAVIGATION_SORT_ORDER);
        String url = params.getString(AdminRequestParameters.URL);

        //Extracting currently selected content from it's url
        Content currentContent = null;
        int currentId = -1;
        if (!"".equals(url)) {
            ContentIdentifier cid = new ContentIdentifier(request, url);
            currentContent = cms.getContent(cid);
            if (currentContent != null) {
                currentId = currentContent.getAssociation().getId();
            }
        }


        String openFoldersList = getOpenFolders(request, currentContent);

        //Setting menu sort order.
        if (sort == null) {
            sort = (String)request.getSession().getAttribute(AdminSessionAttributes.NAVIGATION_SORT_ORDER);
            if (sort == null) {
                sort = ContentProperty.PRIORITY;
            }
        }
        session.setAttribute(AdminSessionAttributes.NAVIGATION_SORT_ORDER, sort);

        int[] openIds = StringHelper.getInts(openFoldersList, ",");
        List<SiteMapEntry> sites = new ArrayList<SiteMapEntry>();
        for (Site site : siteService.getSites()) {
            if (!site.isDisabled()) {
                SiteMapEntry sitemap = cms.getNavigatorMenu(site.getId(), openIds, -1, sort);
                if (sitemap != null) {
                    sitemap.setTitle(site.getName());
                    sites.add(sitemap);
                }
            }
        }

        Map model = new HashMap();
        model.put(AdminRequestParameters.NAVIGATION_SITES, sites);
        model.put(AdminRequestParameters.NAVIGATION_SORT_ORDER, sort);
        model.put(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS, openFoldersList);
        model.put(AdminRequestParameters.THIS_ID, currentId);

        return new ModelAndView(viewName, model);
    }


    /**
     * Get a list of open folders. If "expand" parameter is set, path to select Content object will be added to list.
     * @param request - current request
     * @param currentContent - current selected Content object
     * @return - Comma separated list of open folders
     */
    private String getOpenFolders(HttpServletRequest request, Content currentContent) {

        RequestParameters params = new RequestParameters(request);
        String openFoldersList = params.getString(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS);
        boolean expand = params.getBoolean(AdminRequestParameters.EXPAND, true);

        if (openFoldersList == null || openFoldersList.length() == 0) {
            try {
                ContentIdentifier cid = new ContentIdentifier(request, "/");
                openFoldersList = "0," + cid.getAssociationId();

            } catch (ContentNotFoundException e) {
                openFoldersList = "0";
            }
        }

        // Liste med åpne foldere
        int[] openFolders = StringHelper.getInts(openFoldersList, ",");

        if (expand && currentContent != null) {
            // Vi må legge til id'er slik at treet åpnes og viser denne...

            String path = currentContent.getAssociation().getPath();
            if (path.length() > 1) {
                int pathIds[] = StringHelper.getInts(path, "/");
                if (pathIds != null) {
                    for (int pId : pathIds) {
                        boolean exists = false;
                        for (int openFolder : openFolders) {
                            if (pId == openFolder) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            openFoldersList += "," + pId;
                        }
                    }
                }
            }
        }

        return openFoldersList;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
