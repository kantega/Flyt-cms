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
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.admin.preferences.UserPreferencesManager;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.service.lock.ContentLock;
import no.kantega.publishing.api.service.lock.LockManager;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * A controller which updates breadcrumb and available buttons depending on current page
 */
@Controller
public class ContentPropertiesAction {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertiesAction.class);

    @Autowired private SiteCache aksessSiteCache;
    @Autowired private LinkDao aksessLinkDao;
    @Autowired private UserPreferencesManager userPreferencesManager;
    @Autowired private ContentIdHelper contentIdHelper;
    @Autowired(required = false)
    private OrganizationManager<? extends OrgUnit> organizationManager;
    @Autowired private LockManager lockManager;
    @Autowired
    private ContentTemplateAO contentTemplateAO;

    @RequestMapping("/admin/publish/ContentProperties.action")
    public @ResponseBody Map<String, Object> handleRequest(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String url = request.getParameter("url");
        ContentManagementService cms = new ContentManagementService(request);

        try {
            ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
            Content content = cms.getContent(cid, false);
            SecuritySession securitySession = cms.getSecuritySession();

            List<String> enabledButtons = new ArrayList<>();

            boolean showApproveButtons = false;
            if (content != null) {
                //Breadcrumbs
                List<PathEntry> path = cms.getPathByAssociation(content.getAssociation());
                if (path == null) {
                    path = new ArrayList<>();
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
                List<List<PathEntry>> associations = new ArrayList<>();
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
                }
                if (canApprove) {
                    if (content.getAssociation().getParentAssociationId() != 0) {
                        // Can set display period for all other pages than ROOT page (parentid = 0)
                        enabledButtons.add("DisplayPeriodButton");
                    }
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
                } if (content.getStatus() == ContentStatus.GHOSTDRAFT) {
                    model.put("contentHints", LocaleLabels.getLabel("aksess.navigator.hints.ghostdraft", Aksess.getDefaultAdminLocale()));
                } else if (content.getChangeFromDate() != null) {
                    model.put("contentHints", LocaleLabels.getLabel("aksess.navigator.hints.changefromdate", Aksess.getDefaultAdminLocale()));
                }

                ContentLock lock = lockManager.peekAtLock(content.getId());
                if(lock != null && !lock.getOwner().equals(securitySession.getUser().getId())) {
                    String lockedBy = lock.getOwner();
                    model.put(AdminRequestParameters.PERMISSONS_LOCKED_BY, lockedBy);
                }

                enabledButtons.add("PrivilegesButton");

                Map<String, Object> contentProperties = new HashMap<>();
                contentProperties.put("title", content.getTitle());
                contentProperties.put("alias", content.getAlias());
                contentProperties.put("lastModified", formatDateTime(content.getLastModified()));
                contentProperties.put("lastModifiedBy", content.getLastMajorChangeBy());
                contentProperties.put("approvedBy", content.getApprovedBy());
                contentProperties.put("changeFromDate", formatDateTime(content.getChangeFromDate()));
                contentProperties.put("expireDate", formatDateTime(content.getExpireDate()));
                contentProperties.put("ownerperson", content.getOwnerPerson());
                String owner = trySetOrgunit(content);
                contentProperties.put("owner", owner);
                contentProperties.put("displayTemplate", DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()));
                contentProperties.put("contentTemplate", contentTemplateAO.getTemplateById(content.getContentTemplateId()));

                model.put("showApproveButtons", showApproveButtons);
                model.put("enabledButtons", enabledButtons);

                model.put("contentProperties", contentProperties);
                model.put("userPreferences", userPreferencesManager.getAllPreferences(request));
            }


            return model;


        } catch (SystemException | NotAuthorizedException | ContentNotFoundException e) {
            log.error("", e);
            return null;
        }
    }

    private String trySetOrgunit(Content content) {
        String owner = content.getOwner();
        if (isNotBlank(owner) && organizationManager != null) {
            try {
                OrgUnit ownerUnit = organizationManager.getUnitByExternalId(owner);
                if (ownerUnit != null) {
                    owner = ownerUnit.getName();
                }
            } catch (Exception e) {
                log.info( "Unable to resolve OrgUnit for orgUnitId: " + owner);
            }
        }
        return owner;
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
            log.info( "Unparseable date: " + date);
            return "";
        }
    }
}

