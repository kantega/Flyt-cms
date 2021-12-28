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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.exception.ChildContentNotAllowedException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.templates.AssociationCategoryHelper;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContentAction extends AbstractController {
    private String view;

    private TemplateConfigurationCache templateConfigurationCache;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Resource(name = "contentListenerNotifier")
    private ContentEventListener contentEventListener;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        RequestParameters param = new RequestParameters(request);

        ContentManagementService aksessService = new ContentManagementService(request);
        SecuritySession securitySession = aksessService.getSecuritySession();

        String url = request.getParameter("url");
        ContentIdentifier cidParent = contentIdHelper.fromRequestAndUrl(request, url);
        Content parent = aksessService.getContent(cidParent);
        if (parent == null) {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }

        if (!securitySession.isAuthorized(parent, Privilege.UPDATE_CONTENT)) {
            throw new NotAuthorizedException(securitySession.getUser().getId() + " not authorized to edit:" + parent.getTitle());
        }

        model.put("parent", parent);

        ContentTemplate parentTemplate = aksessService.getContentTemplate(parent.getContentTemplateId());

        AssociationCategoryHelper helper = new AssociationCategoryHelper(templateConfigurationCache);
        List<AssociationCategory> allowedAssociations = helper.getAllowedAssociationCategories(parentTemplate);
        if (allowedAssociations.size() == 0) {
            throw new ChildContentNotAllowedException();
        }

        model.put("allowedAssociations", allowedAssociations);


        String addedParents = param.getString("addedParents");
        if (addedParents == null) {
            addedParents = "";
        }
        model.put("addedParents", addedParents);

        List allowedTemplates = aksessService.getAllowedChildTemplates(parent.getAssociation().getSiteId(), parent.getContentTemplateId());
        model.put("allowedTemplates", allowedTemplates);

        // Create an associations for all places where the parentpage has associations
        List<Association> parentAssociations = parent.getAssociations();
        List<Association> associations = new ArrayList<Association>();
        for (Association parentAssociation : parentAssociations) {
            associations.add(parentAssociation);
        }

        // Add associations (parents) added by user ...
        if (addedParents.length() > 0) {
            int parents[] = StringHelper.getInts(addedParents, ",");
            for (int parent1 : parents) {
                boolean found = false;

                // Only add those that does not exist
                for (Association association : associations) {
                    if (parent1 == association.getAssociationId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ContentIdentifier cid =  ContentIdentifier.fromAssociationId(parent1);
                    Content association = aksessService.getContent(cid);
                    if (association != null) {
                        // Check if user is authorized to publish here
                        if (securitySession.isAuthorized(association, Privilege.APPROVE_CONTENT)) {
                            Association a = aksessService.getAssociationById(parent1);
                            associations.add(a);
                        } else {
                            model.put("notAuthorized", Boolean.TRUE);
                        }
                    }
                }
            }
        }

        model.put("associations", associations);

        List<Site> sites = templateConfigurationCache.getTemplateConfiguration().getSites();
        boolean displayAddAssociation = sites.size() > 1;

        Configuration config = Aksess.getConfiguration();
        model.put("displayAddAssociation", config.getBoolean("admin.addassociation.display", displayAddAssociation));

        // Run plugins
        contentEventListener.beforeSelectTemplate(new ContentEvent()
                .setModel(model)
                .setUser(securitySession.getUser()));

        // Show page where user selects template etc
        return new ModelAndView(view, model);
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setView(String view) {
        this.view = view;
    }
}
