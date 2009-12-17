/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContentPropertiesAction implements Controller {

    @Autowired private SiteCache aksessSiteCache;
    @Autowired private LinkDao aksessLinkDao;
    @Autowired private View aksessJsonView;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String url = request.getParameter("url");
        ContentManagementService cms = new ContentManagementService(request);

        try {
            ContentIdentifier cid = new ContentIdentifier(url);
            Content content = cms.getContent(cid, false);
            SecuritySession securitySession = SecuritySession.getInstance(request);

            List<String> enabledButtons = new ArrayList<String>();

            if (content != null) {

                //Breadcrumbs
                List<PathEntry> path = cms.getPathByAssociation(content.getAssociation());
                if (path == null) {
                    path = new ArrayList<PathEntry>();
                }

                //Add current element to the path.
                PathEntry current = new PathEntry(content.getAssociation().getId(), content.getTitle());
                path.add(current);
    
                //Change the title of the root element (site) to site alias to match the navigator title.
                int siteId = content.getAssociation().getSiteId();
                path.get(0).setTitle(aksessSiteCache.getSiteById(siteId).getName());

                model.put("path", path);


                //Broken links
                model.put("links", aksessLinkDao.getLinksforContentId(content.getId()));


                //Associations
                List<Association> associations = new ArrayList<Association>();
                for (Association association : content.getAssociations()) {
                    if (association.getAssociationtype() != AssociationType.SHORTCUT) {
                        associations.add(association);
                    }
                }
                model.put("associations", associations);


                boolean canUpdate = securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT);
                boolean canApprove = securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT);
                if (canUpdate || canApprove) {
                    enabledButtons.add("NewSubpageButton");
                    enabledButtons.add("EditPageButton");
                    enabledButtons.add("DisplayPeriodButton");
                }
                if (canApprove) {
                    enabledButtons.add("DeletePageButton");
                    enabledButtons.add("CutButton");
                    enabledButtons.add("CopyButton");
                    enabledButtons.add("PasteButton");
                }
                enabledButtons.add("PrivilegesButton");
            }
            model.put("enabledButtons", enabledButtons);

            return new ModelAndView(aksessJsonView, model);


        } catch (ContentNotFoundException e) {
            Log.error(this.getClass().getName(), e, null, null);
            return null;
        } catch (SystemException e) {
            Log.error(this.getClass().getName(), e, null, null);
            return null;
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e, null, null);
            return null;
        }
    }


}
