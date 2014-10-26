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

package no.kantega.publishing.common.util.templates;

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.api.model.PublicIdObject;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 5, 2009
 * Time: 3:34:56 PM
 *
 * Used to export database templates configuration to XML based configuration
 *
 * TODO: Delete in future version
 *
 */
public class TemplateConfigurationExportHelper {
    private static String SOURCE = "aksess.TemplateConfigurationExportHelper";

    /**
     * Returns a templateconfiguration from database - used to export old versions to XML
     * @return - TemplateConfiguration
     * @throws SystemException
     */
    public static TemplateConfiguration getConfigururationFromDatabase() throws SystemException {
        TemplateConfiguration tc = new TemplateConfiguration();

        Connection c = dbConnectionFactory.getConnection();

        try {
            tc.setSites(getSites(c));
            tc.setAssociationCategories(getAssociationCategories(c));
            tc.setContentTemplates(getContentTemplates(c, AttributeDataType.CONTENT_DATA));
            tc.setMetadataTemplates(getContentTemplates(c, AttributeDataType.META_DATA));
            tc.setDisplayTemplates(getDisplayTemplates(c));
            tc.setDocumentTypes(getDocumentTypes(c));


            // We want to export public ids, get them
            for (ContentTemplate template : tc.getContentTemplates()) {
                for (ContentTemplate parent : template.getAllowedParentTemplates()) {
                    updateIds(tc.getContentTemplates(), parent);
                }

                for (AssociationCategory a : template.getAssociationCategories()) {
                    updateIds(tc.getAssociationCategories(), a);
                }
                updateIds(tc.getAssociationCategories(), template.getDefaultAssociationCategory());

                updateIds(tc.getDocumentTypes(), template.getDocumentType());
                updateIds(tc.getDisplayTemplates(), template.getDocumentTypeForChildren());
            }

            for (DisplayTemplate dt : tc.getDisplayTemplates()) {
                updateIds(tc.getContentTemplates(), dt.getContentTemplate());
                updateIds(tc.getMetadataTemplates(), dt.getMetaDataTemplate());

                for (Site s : dt.getSites()) {
                    updateIds(tc.getSites(), s);
                }
            }

        } catch (Exception e) {
            Log.error("TemplateConfigurationExportHelper", e, null, null);
        } finally {
            try {
                c.close();
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }

        }
        return tc;

    }

    private static List<AssociationCategory> getAssociationCategories(Connection c) throws SQLException {
        List<AssociationCategory> categories = new ArrayList<AssociationCategory>();
        ResultSet rs = SQLHelper.getResultSet(c, "select * from associationcategory");
        while (rs.next()) {
            AssociationCategory category = new AssociationCategory();
            category.setId(rs.getInt("AssociationId"));
            category.setName(rs.getString("AssociationName"));
            category.setDescription(rs.getString("Description"));
            String publicId = rs.getString("PublicId");
            if (publicId == null || publicId.length() == 0) publicId = category.getName();
            category.setPublicId(publicId);
            categories.add(category);
        }
        rs.close();
        return categories;
    }

    private static List<Site> getSites(Connection c) throws SQLException {
        List<Site> sites = new ArrayList<Site>();
        ResultSet rs = SQLHelper.getResultSet(c, "select * from sites");
        while (rs.next()) {
            Site site = new Site();
            site.setId(rs.getInt("SiteId"));
            site.setName(rs.getString("Name"));
            site.setAlias(rs.getString("Alias"));
            String publicId = site.getAlias();
            if (publicId.length() > 1) {
                publicId = publicId.substring(1, publicId.length() - 1);
            }
            site.setPublicId(publicId);
            sites.add(site);
        }
        rs.close();
        return sites;
    }


    private static List<DocumentType> getDocumentTypes(Connection c) throws SQLException {
        ResultSet rs;
        rs = SQLHelper.getResultSet(c, "select * from documenttype");
        List<DocumentType> doctypes = new ArrayList<DocumentType>();
        while (rs.next()) {
            DocumentType doctype = new DocumentType(rs.getInt("Id"), rs.getString("Name"));
            doctype.setPublicId(doctype.getName());
            doctypes.add(doctype);
        }
        rs.close();
        return doctypes;
    }

    private static List<DisplayTemplate> getDisplayTemplates(Connection c) throws SQLException {
        ResultSet rs;
        rs = SQLHelper.getResultSet(c, "select * from displaytemplates");
        List<DisplayTemplate> displayTemplates = new ArrayList<DisplayTemplate>();
        while(rs.next()) {
            DisplayTemplate template = new DisplayTemplate();
            template.setId(rs.getInt("DisplayTemplateId"));
            int siteId = rs.getInt("Site");
            if (siteId > 0) {
                List<Site> sites = new ArrayList<Site>();
                Site site = new Site();
                site.setId(siteId);
                sites.add(site);              
                template.setSites(sites);
            }
            int contentTemplateId = rs.getInt("ContentTemplateId");
            ContentTemplate ct =  new ContentTemplate();
            ct.setId(contentTemplateId);
            template.setContentTemplate(ct);

            int metadataTemplateId = rs.getInt("MetadataTemplateId");
            if (metadataTemplateId != -1) {
                ContentTemplate mt =  new ContentTemplate();
                mt.setId(metadataTemplateId);
                template.setMetaDataTemplate(mt);
            }

            template.setName(rs.getString("Name"));
            template.setDescription(rs.getString("Description"));
            template.setImage(rs.getString("Image"));
            template.setView(rs.getString("URLFullView"));
            String miniview = rs.getString("URLMiniView");
            if (miniview == null) miniview = "";
            template.setMiniView(miniview);
            template.setAllowMultipleUsages(rs.getInt("AllowMultipleUsages") == 1 ? true : false);
            template.setIsNewGroup(rs.getInt("IsNewGroup") == 1 ? true : false);
            long defaultForumId = rs.getLong("DefaultForumId");
            if (defaultForumId >= 0) {
                template.setDefaultForumId(defaultForumId);
            }
            template.setPublicId(rs.getString("PublicId"));
            if (template.getPublicId() == null || template.getPublicId().length() == 0) {
                template.setPublicId(template.getName());
            }

            List<DisplayTemplateControllerId> controllers = new ArrayList<DisplayTemplateControllerId>();
            template.setControllers(controllers);

            ResultSet controllersRs = SQLHelper.getResultSet(c, "select * from displaytemplatecontroller where displaytemplateid = " + template.getId());
            while (controllersRs.next()) {
                DisplayTemplateControllerId controller = new DisplayTemplateControllerId();
                controller.setId(controllersRs.getString("controllername"));
                template.getControllers().add(controller);
            }
            controllersRs.close();

            displayTemplates.add(template);
        }
        rs.close();
        return displayTemplates;
    }

    private static List getContentTemplates(Connection c, int type) throws SQLException {
        ResultSet rs;
        rs = SQLHelper.getResultSet(c, "select * from contenttemplates where TemplateType = " + type);
        List contentTemplates = new ArrayList();
        while(rs.next()) {
            ContentTemplate template = new ContentTemplate();

            template.setId(rs.getInt("ContentTemplateId"));
            template.setName(rs.getString("Name"));
            template.setContentType(ContentType.getContentTypeAsEnum(rs.getInt("ContentType")));
            int docTypeId = rs.getInt("DefaultDocumentTypeId");
            if (docTypeId > 0) {
                template.setDocumentType(new DocumentType(docTypeId));
            }

            template.setTemplateFile(rs.getString("URL"));
            int expireMonths = rs.getInt("ExpireMonths");
            if (expireMonths > 0) {
                template.setExpireMonths(expireMonths);
            }
            int expireAction = rs.getInt("ExpireAction");
            if (expireAction > 0) {
                template.setExpireAction(expireAction);
            }
            int hearing = rs.getInt("HearingEnabled");
            if (hearing == 1) {
                template.setHearingEnabled(true);
            }
            int keepVersions = rs.getInt("KeepVersions");
            if (keepVersions != 0) {
                template.setKeepVersions(keepVersions);
            }
            int docTypeForChildren = rs.getInt("DefaultDocTypeIdForChildren");
            if (docTypeForChildren > 0) {
                template.setDocumentTypeIdForChildren(new DocumentType(docTypeForChildren));
            }

            AssociationCategory category = new AssociationCategory();
            category.setId(rs.getInt("DefaultAssociationCategoryId"));
            if (category.getId() > 0) {
                template.setDefaultAssociationCategory(category);
            }

            template.setPublicId(rs.getString("PublicId"));
            if (template.getPublicId() == null || template.getPublicId().length() == 0) {
                template.setPublicId(template.getName());
            }


            if (type == AttributeDataType.CONTENT_DATA) {

                List allowedParentTemplates = new ArrayList();
                ResultSet parentsRs = SQLHelper.getResultSet(c, "select * from ct2parent where TemplateId = " + template.getId());
                while (parentsRs.next()) {
                    ContentTemplate parent = new ContentTemplate();
                    parent.setId(parentsRs.getInt("ParentId"));
                    allowedParentTemplates.add(parent);
                }
                template.setAllowedParentTemplates(allowedParentTemplates);
                parentsRs.close();

                List allowedCategories = new ArrayList();
                ResultSet categoriesRs = SQLHelper.getResultSet(c, "select * from ct2association where TemplateId = " + template.getId());
                while (categoriesRs.next()) {
                    AssociationCategory acategory = new AssociationCategory();
                    acategory.setId(categoriesRs.getInt("AssociationId"));
                    allowedCategories.add(acategory);
                }
                template.setAssociationCategories(allowedCategories);
                categoriesRs.close();
            }

            contentTemplates.add(template);
        }
        rs.close();
        return contentTemplates;
    }

    private static void updateIds(List objects, PublicIdObject object) {
        if (object == null) {
            return;
        }

        for (int i = 0; i < objects.size(); i++) {
            PublicIdObject o = (PublicIdObject)objects.get(i);
            if (o.getId() == object.getId()) {
                object.setPublicId(o.getPublicId());
                object.setId(-1);
            }
        }
    }
}
