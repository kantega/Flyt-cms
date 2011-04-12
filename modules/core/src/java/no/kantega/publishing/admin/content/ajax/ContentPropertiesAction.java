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
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.dwr.ContentClipboardHandler;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.lock.ContentLock;
import no.kantega.publishing.common.service.lock.LockManager;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.spring.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A controller which updates breadcrumb and available buttons depending on current page
 */
public class ContentPropertiesAction implements Controller {

    @Autowired private SiteCache aksessSiteCache;
    @Autowired private LinkDao aksessLinkDao;
    @Autowired private UserPreferencesManager userPreferencesManager;
    private View aksessJsonView;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String url = request.getParameter("url");
        ContentManagementService cms = new ContentManagementService(request);

        try {
            ContentIdentifier cid = new ContentIdentifier(request, url);
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
                model.put("links",  aksessLinkDao.getBrokenLinksforContentId(content.getId()));


                //Associations
                List<List<PathEntry>> associations = new ArrayList<List<PathEntry>>();
                for (Association association : content.getAssociations()) {
                    if (association.getAssociationtype() != AssociationType.SHORTCUT) {
                        //Retrieve the path down to this association
                        List<PathEntry> paths = cms.getPathByAssociation(association);
                        //Add the association itself to the path. 
                        PathEntry leaf = new PathEntry(association.getId(), content.getTitle());
                        paths.add(leaf);
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
                    Clipboard contentClipboard = (Clipboard) request.getSession().getAttribute(AdminSessionAttributes.CLIPBOARD_CONTENT);
                    if (contentClipboard != null && !contentClipboard.isEmpty()){
                        enabledButtons.add("PasteButton");
                    }
                }

                // Content hints for publisher
                if (content.getStatus() == ContentStatus.DRAFT) {
                    model.put("contentHints", LocaleLabels.getLabel("aksess.navigator.hints.draft", Aksess.getDefaultAdminLocale()));
                } else if (content.getChangeFromDate() != null) {
                    model.put("contentHints", LocaleLabels.getLabel("aksess.navigator.hints.changefromdate", Aksess.getDefaultAdminLocale()));
                }                

                ContentLock lock = LockManager.peekAtLock(content.getId());
                if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
                    String lockedBy = lock.getOwner();
                    model.put(AdminRequestParameters.PERMISSONS_LOCKED_BY, lockedBy);
                }

                enabledButtons.add("PrivilegesButton");
            }

            Map<String, Object> contentProperties = new HashMap<String, Object>();
            contentProperties.put("title", content.getTitle());
            contentProperties.put("alias", content.getAlias());
            contentProperties.put("lastModified", formatDateTime(content.getLastModified()));
            contentProperties.put("lastModifiedBy", content.getLastMajorChangeBy());
            contentProperties.put("approvedBy", content.getApprovedBy());
            contentProperties.put("changeFromDate", formatDateTime(content.getChangeFromDate()));
            contentProperties.put("expireDate", formatDateTime(content.getExpireDate()));
            contentProperties.put("ownerperson", content.getOwnerPerson());
            String owner = content.getOwner();
            if (owner != null && owner.trim().length() > 0) {
                Map orgManagers = RootContext.getInstance().getBeansOfType(OrganizationManager.class);
                if (orgManagers != null && orgManagers.size() > 0) {
                    OrganizationManager orgManager = (OrganizationManager) orgManagers.values().iterator().next();
                    try {
                        OrgUnit ownerUnit = orgManager.getUnitByExternalId(owner);
                        if (ownerUnit != null) {
                            owner = ownerUnit.getName();
                        }
                    } catch (Exception e) {
                        Log.info(this.getClass().getName(), "Unable to resolve OrgUnit for orgUnitId: " + owner);
                    }
                }
            }
            contentProperties.put("owner", owner);
            contentProperties.put("displayTemplate", DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()));

            model.put("showApproveButtons", showApproveButtons);
            model.put("enabledButtons", enabledButtons);

            model.put("contentProperties", contentProperties);
            model.put("userPreferences", userPreferencesManager.getAllPreferences(request));


            return new ModelAndView(aksessJsonView, model);


        } catch (ContentNotFoundException e) {
            // Do nothing
            return null;
        } catch (SystemException e) {
            Log.error(this.getClass().getName(), e, null, null);
            return null;
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e, null, null);
            return null;
        }
    }

    public void setAksessJsonView(View aksessJsonView) {
        this.aksessJsonView = aksessJsonView;
    }

    private String formatDateTime(Date date) {
        return format(date, Aksess.getDefaultDatetimeFormat());
    }

    private String format(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(date);
        } catch (Exception e) {
            Log.info(this.getClass().getName(), "Unparseable date: " + date, null, null);
            return "";
        }
    }
}

