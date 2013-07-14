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

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.administration.action.CreateRootAction;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigateController extends AbstractContentAction {
    private String view;

    private SiteCache siteCache;
    private CreateRootAction createRootAction;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(true);
        ContentManagementService aksessService = new ContentManagementService(request);
        RequestParameters param = new RequestParameters(request);

        String url = param.getString(AdminRequestParameters.URL);

        Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);

        if (url != null || request.getParameter(AdminRequestParameters.THIS_ID) != null || request.getParameter(AdminRequestParameters.CONTENT_ID) != null) {
            ContentIdentifier cid = null;
            if (url != null) {
                cid = ContentIdHelper.fromRequestAndUrl(request, url);
            } else {
                cid = ContentIdHelper.fromRequest(request);
            }
            current = aksessService.getContent(cid);
        }

        if (current == null ) {
            ContentIdentifier cid = null;
            try {
                // No current object, go to start page
                cid = ContentIdHelper.fromRequestAndUrl(request, "/");
            } catch (ContentNotFoundException cnfe) {
                // Start page has not been created
                Site site = siteCache.getSiteByHostname(request.getServerName());
                if (site == null) {
                    List<Site> sites = siteCache.getSites();
                    if (sites.size() == 0) {
                        throw new ConfigurationException("No sites defined in template configuration (aksess-templateconfig.xml)");
                    }
                    site = sites.get(0);
                }
                createRootAction.createRootPage(site.getId(), request);
                cid = ContentIdHelper.fromRequestAndUrl(request, "/");
            }
            current = aksessService.getContent(cid);
        }

        session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, current);

        Content editedContent = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);


        String currentUrl = current.getUrl();
        Map<String, Object> model = new HashMap<>();
        if (editedContent != null && editedContent.isModified()) {
            // User is editing a page and has modified it, show preview 
            currentUrl = request.getContextPath() + "/admin/publish/ViewContentPreviewFrame.action?thisId=";
            if (editedContent.isNew()) {
                // New page
                currentUrl += editedContent.getAssociation().getParentAssociationId();
            } else {
                currentUrl += editedContent.getAssociation().getId();
            }

            setRequestVariables(request, editedContent, aksessService, model);
            Log.debug(this.getClass().getName(), "User is editing page:" + editedContent.getTitle(), null, null);
        }

        model.put("sites", siteCache.getSites());
        model.put("currentUrl", currentUrl);        

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setCreateRootAction(CreateRootAction createRootAction) {
        this.createRootAction = createRootAction;
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
