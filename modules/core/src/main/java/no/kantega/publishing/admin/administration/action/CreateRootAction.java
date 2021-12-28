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

package no.kantega.publishing.admin.administration.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.MissingTemplateException;
import no.kantega.publishing.common.exception.RootExistsException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateRootAction  extends AbstractController {

    private SiteCache siteCache;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");
        int siteId = param.getInt("siteId");
        createRootPage(siteId, request);
        return new ModelAndView(new RedirectView("ListSites.action"));
    }

    public void createRootPage(int siteId, HttpServletRequest request) throws SQLException, RootExistsException, MissingTemplateException, SystemException, NotAuthorizedException {
        ContentManagementService aksessService = new ContentManagementService(request);

        Site site = siteCache.getSiteById(siteId);
        if (site == null) {
            throw new InvalidParameterException("siteId");
        }

        Content content = new Content();

        content.setAlias(site.getAlias());

        content.setTitle(site.getName());
        content.setType(ContentType.PAGE);

        // Legg til hovedknytning
        Association association = new Association();
        association.setContentId(-1);
        association.setParentAssociationId(0);
        association.setSiteId(siteId);
        association.setAssociationtype(AssociationType.DEFAULT_POSTING_FOR_SITE);
        association.setCategory(new AssociationCategory(0));
        content.addAssociation(association);

        // Hvis basen ikke inneholder noen sider for siten, kan hjemmesida opprettes
        try(Connection c = dbConnectionFactory.getConnection()) {

            ResultSet rs = SQLHelper.getResultSet(c, "select * from associations where SiteId = " + site.getId());
            if (rs.next()) {
                throw new RootExistsException("Hjemmesiden er allerede opprettet");
            }

            rs = SQLHelper.getResultSet(c, "select * from content where Alias = '" + site.getAlias() + "'");
            if (rs.next()) {
                throw new RootExistsException("Finnes allerede en side med dette aliaset");
            }

            // Find homepage
            String frontpageUrl = "/WEB-INF/jsp" + site.getAlias() +"index.jsp";

            DisplayTemplate displayTemplate = null;

            // If a site has specified a display template id, try to look this template
            TemplateConfiguration templateConfiguration = TemplateConfigurationCache.getInstance().getTemplateConfiguration();
            if (site.getPublicId() != null && site.getPublicId().length() > 0) {
                for(DisplayTemplate template : templateConfiguration.getDisplayTemplates()) {
                    if(template.getPublicId().equalsIgnoreCase(site.getDisplayTemplateId())) {
                        displayTemplate = template;
                        break;
                    }
                }                            
            }
            // No template specified or found, try to look a template in the folder specified by the alias
            if (displayTemplate == null) {
                for(DisplayTemplate template : templateConfiguration.getDisplayTemplates()) {
                    if(frontpageUrl.equals(template.getView())) {
                        displayTemplate = template;
                        break;
                    }
                }
            }
            if (displayTemplate == null) {
                throw new MissingTemplateException("Can't find display template for site " + site.getAlias());
            }

            content.setDisplayTemplateId(displayTemplate.getId());
            content.setContentTemplateId(displayTemplate.getContentTemplate().getId());
            if (displayTemplate.getMetaDataTemplate() != null) {
                content.setMetaDataTemplateId(displayTemplate.getMetaDataTemplate().getId());
            }

            aksessService.checkInContent(content, ContentStatus.PUBLISHED);

        }
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
