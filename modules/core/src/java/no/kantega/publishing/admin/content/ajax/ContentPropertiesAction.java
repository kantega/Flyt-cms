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
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.common.service.lock.ContentLock;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.admin.preferences.UserPreference;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import no.kantega.publishing.admin.AdminRequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * A controller which updates breadcrumb and available buttons depending on current page
 */
public class ContentPropertiesAction implements Controller {

    @Autowired private SiteCache aksessSiteCache;
    @Autowired private LinkDao aksessLinkDao;
    @Autowired private View aksessJsonView;
    @Autowired private UserPreferencesManager userPreferencesManager;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String url = request.getParameter("url");
        ContentManagementService cms = new ContentManagementService(request);

        try {
            ContentIdentifier cid = new ContentIdentifier(url);
            Content content = cms.getContent(cid, false);
            SecuritySession securitySession = SecuritySession.getInstance(request);

            List<String> enabledButtons = new ArrayList<String>();

            boolean showApproveButtons = false;
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
                List<List<PathEntry>> associations = new ArrayList<List<PathEntry>>();
                for (Association association : content.getAssociations()) {
                    if (association.getAssociationtype() != AssociationType.SHORTCUT) {
                        List<PathEntry> paths = cms.getPathByAssociation(association);
                        paths.add(current);
                        associations.add(paths);
                    }
                }
                model.put("associations", associations);


                boolean canUpdate = securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT);
                boolean canApprove = securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT);
                if (canUpdate || canApprove) {
                    enabledButtons.add("NewSubpageButton");
                    enabledButtons.add("EditPageButton");
                    if (content.getAssociation().getParentAssociationId() != 0) {
                        // Can set display period for all other pages than ROOT page (parentid = 0)
                        enabledButtons.add("DisplayPeriodButton");
                    }
                }
                if (canApprove) {
                    enabledButtons.add("DeletePageButton");
                    enabledButtons.add("CutButton");
                    enabledButtons.add("CopyButton");
                    if (content.getStatus() == ContentStatus.WAITING_FOR_APPROVAL) {
                        showApproveButtons = true;
                    }                                            
                }

                ContentLock lock = LockManager.peekAtLock(content.getId());
                if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
                    String lockedBy = lock.getOwner();
                    // TODO: Do something with this
                    model.put(AdminRequestParameters.PERMISSONS_LOCKED_BY, lockedBy);
                }

                enabledButtons.add("PrivilegesButton");
            }

            model.put("showApproveButtons", showApproveButtons);
            model.put("enabledButtons", enabledButtons);

            model.put("content", content);
            model.put("sites",  aksessSiteCache.getSites());
            model.put("userPreferences", userPreferencesManager.getAllPreferences(request));


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



    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }

}
