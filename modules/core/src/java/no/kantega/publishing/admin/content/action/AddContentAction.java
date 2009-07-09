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

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.util.LocaleLabels;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.AssociationCategoryCache;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.ChildContentNotAllowedException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.event.ContentListenerUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Nov 12, 2007
 * Time: 2:56:05 PM
 */
public class AddContentAction implements Controller {
    private String view;

    private TemplateConfigurationCache templateConfigurationCache;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        RequestParameters param = new RequestParameters(request);

        ContentManagementService aksessService = new ContentManagementService(request);
        SecuritySession securitySession = SecuritySession.getInstance(request);

        ContentIdentifier cidParent = new ContentIdentifier(request);
        Content parent = aksessService.getContent(cidParent);
        if (parent == null) {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }

        model.put("parent", parent);

        ContentTemplate parentTemplate = aksessService.getContentTemplate(parent.getContentTemplateId());

        List<AssociationCategory> allowedAssociations = getAllowedAssociationCategories(parentTemplate);
        model.put("allowedAssociations", allowedAssociations);


        String addedParents = param.getString("addedParents");
        if (addedParents == null) {
            addedParents = "";
        }
        model.put("addedParents", addedParents);

        List allowedTemplates = aksessService.getAllowedChildTemplates(parent.getAssociation().getSiteId(), parent.getContentTemplateId());
        model.put("allowedTemplates", allowedTemplates);

        // Create an associations for all places where the parentpage has associations
        List parentAssociations = parent.getAssociations();
        List associations = new ArrayList();
        for (int i = 0; i < parentAssociations.size(); i++) {
            associations.add(parentAssociations.get(i));
        }

        // Add associations (parents) added by user ...
        if (addedParents.length() > 0) {
            int parents[] = StringHelper.getInts(addedParents, ",");
            for (int i = 0; i < parents.length; i++) {
                boolean found = false;

                // Only add those that does not exist
                for (int j = 0; j < associations.size(); j++) {
                    Association tmp = (Association)associations.get(j);
                    if (parents[i] == tmp.getAssociationId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ContentIdentifier cid = new ContentIdentifier();
                    cid.setAssociationId(parents[i]);
                    Content tmp = aksessService.getContent(cid);
                    if (tmp != null) {
                        // Check if user is authorized to publish here
                        if (securitySession.isAuthorized(tmp, Privilege.APPROVE_CONTENT)) {
                            Association a = aksessService.getAssociationById(parents[i]);
                            associations.add(a);
                        } else {
                            model.put("error", LocaleLabels.getLabel("aksess.selecttemplate.ikketilgang", Aksess.getDefaultAdminLocale()));
                        }
                    }
                }
            }
        }

        model.put("associations", associations);

        // Run plugins
        ContentListenerUtil.getContentNotifier().beforeSelectTemplate(model);

        // Show page where user selects template etc
        return new ModelAndView(view, model);
    }

    private List<AssociationCategory> getAllowedAssociationCategories(ContentTemplate parentTemplate) throws ChildContentNotAllowedException {
        List<AssociationCategory> tmpAllowedAssociations = parentTemplate.getAssociationCategories();
        if (tmpAllowedAssociations == null || tmpAllowedAssociations.size() == 0) {
            throw new ChildContentNotAllowedException();
        } else if (parentTemplate.getContentType() != ContentType.PAGE) {
            throw new ChildContentNotAllowedException();
        }

        // Template only holds id of AssociationCategory, get complete AssociationCategory from cache
        List<AssociationCategory> allAssociations = templateConfigurationCache.getTemplateConfiguration().getAssociationCategories();
        List<AssociationCategory> allowedAssociations = new ArrayList<AssociationCategory>();
        for (AssociationCategory allowedAssociation : tmpAllowedAssociations) {
            for (AssociationCategory allAssociation : allAssociations) {
                if (allAssociation.getId() == allowedAssociation.getId()) {
                    allowedAssociations.add(allAssociation);
                }
            }
        }
        return allowedAssociations;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
