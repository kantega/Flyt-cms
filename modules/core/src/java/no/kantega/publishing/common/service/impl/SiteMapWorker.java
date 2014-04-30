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

package no.kantega.publishing.common.service.impl;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SiteMapWorker {

    private static SiteMapEntry getFirst(int parentId, List<SiteMapEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            SiteMapEntry e = entries.get(i);
            // Snarveier kan aldri være parents
            if (e.parentId == parentId) {
                entries.remove(e);
                return e;
            }
        }
        return null;
    }

    private static void addToSiteMap(SiteMapEntry parent, List<SiteMapEntry> entries) {
        if (parent.getType() == ContentType.PAGE) {
            int parentId = parent.currentId;
            SiteMapEntry entry = getFirst(parentId, entries);
            while (entry != null) {
                // Legger kun til hovedknytninger, ellers kan ting gå i evig løkke...
                addToSiteMap(entry, entries);
                parent.addChild(entry);
                entry = getFirst(parentId, entries);
            }
        }
    }


    public static SiteMapEntry getSiteMapBySQL(StringBuilder where, int rootId, boolean getAll, String sort) throws SystemException {
        List<SiteMapEntry> tmpentries = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select content.ContentId, content.ContentType, content.Alias, content.VisibilityStatus, content.NumberOfNotes, content.Location, content.OpenInNewWindow, content.Owner, content.OwnerPerson, content.IsSearchable, content.ContentTemplateId, content.DisplayTemplateId, contentversion.Status, contentversion.Title, contentversion.AltTitle, contentversion.LastModified, associations.UniqueId, associations.AssociationId, associations.ParentAssociationId, associations.Type, associations.Category, associations.SecurityId, content.GroupId from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.ContentId = associations.ContentId and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0)");
        query.append(where);
        if (!getAll) {
            query.append(" and contentversion.Status = ").append(ContentStatus.PUBLISHED.getTypeAsInt());
            query.append(" and (content.VisibilityStatus = ").append(ContentVisibilityStatus.ACTIVE.statusId).append(")");
        }
        query.append(" order by associations.ParentAssociationId ");

        if (ContentProperty.TITLE.equals(sort)) {
            query.append(", contentversion.Title");
        } else if (ContentProperty.LAST_MODIFIED.equals(sort)) {
            query.append(", contentversion.LastModified desc");
        } else {
            query.append(", associations.Category, associations.Priority");
        }

        SiteMapEntry sitemap = null;

        try (Connection c = dbConnectionFactory.getConnection()) {
            ResultSet rs = SQLHelper.getResultSet(c, query.toString());
            while(rs.next()) {
                int p = 1;
                int contentId = rs.getInt(p++);
                ContentType type = ContentType.getContentTypeAsEnum(rs.getInt(p++));
                String alias = rs.getString(p++);
                int visibilityStatus = rs.getInt(p++);
                int numberOfNotes = rs.getInt(p++);
                String location = rs.getString(p++);
                boolean openInNewWindow = rs.getInt(p++) == 1;
                String owner = rs.getString(p++);
                String ownerPerson = rs.getString(p++);
                boolean isSearchable = rs.getInt(p++) == 1;
                int contentTemplateId = rs.getInt(p++);
                int displayTemplateId = rs.getInt(p++);
                ContentStatus status  = ContentStatus.getContentStatusAsEnum(rs.getInt(p++));
                String title = rs.getString(p++);
                String altTitle = rs.getString(p++);
                Date lastModified = rs.getDate(p++);

                int uniqueId = rs.getInt(p++);
                int currentId = rs.getInt(p++);
                int parentId = rs.getInt(p++);
                int aType = rs.getInt(p++);
                int aCategory = rs.getInt(p++);
                int aSecId = rs.getInt(p++);
                int groupId = rs.getInt(p);
                if (aType == AssociationType.SHORTCUT) {
                    type = ContentType.SHORTCUT;
                }
                if ((rootId == -1 && parentId == 0 && type != ContentType.SHORTCUT) || (rootId == uniqueId && type != ContentType.SHORTCUT)) {
                    sitemap = new SiteMapEntry();
                    sitemap.setUniqueId(uniqueId);
                    sitemap.setCurrentId(currentId);
                    sitemap.setParentId(parentId);
                    sitemap.setType(type);
                    sitemap.setStatus(status);
                    sitemap.setVisibilityStatus(visibilityStatus);
                    sitemap.setTitle(title);
                    sitemap.setNumberOfNotes(numberOfNotes);
                    if (alias != null && alias.length() > 0) {
                        sitemap.setAlias(alias);
                    }
                    sitemap.setAltTitle(altTitle);
                    sitemap.setAssociationCategory(aCategory);
  	                sitemap.setGroupId(groupId);
                    sitemap.setContentId(contentId);
                    sitemap.setLastModified(lastModified);
                    sitemap.setOwner(owner);
                    sitemap.setOwnerPerson(ownerPerson);
                    sitemap.setSecurityId(aSecId);
                    sitemap.setSearchable(isSearchable);
                    sitemap.setContentTemplateId(contentTemplateId);
                    sitemap.setDisplayTemplateId(displayTemplateId);
                } else {
                    SiteMapEntry entry = new SiteMapEntry();
                    entry.setUniqueId(uniqueId);
                    entry.setCurrentId(currentId);
                    entry.setParentId(parentId);
                    entry.setType(type);
                    entry.setStatus(status);
                    entry.setVisibilityStatus(visibilityStatus);
                    entry.setTitle(title);
                    entry.setAltTitle(altTitle);
                    entry.setNumberOfNotes(numberOfNotes);
                    if (alias != null && alias.length() > 0) {
                        entry.setAlias(alias);
                    }
                    entry.setAssociationCategory(aCategory);
  	                entry.setGroupId(groupId);
                    entry.setContentId(contentId);
                    entry.setLastModified(lastModified);
                    entry.setOwner(owner);
                    entry.setOwnerPerson(ownerPerson);
                    entry.setSecurityId(aSecId);
                    entry.setSearchable(isSearchable);
                    entry.setContentTemplateId(contentTemplateId);
                    entry.setDisplayTemplateId(displayTemplateId);
                    if (type == ContentType.LINK) {
                        // Enten har bruker angitt at lenke skal åpnes i eget vindu eller så skal dette skje automatisk
                        if (openInNewWindow || (location != null && location.length() > 0 && location.charAt(0) != '/')) {
                            entry.setOpenInNewWindow(true);
                        }
                    }

                    tmpentries.add(entry);
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        if (sitemap != null) {
            // Vi har funnet starten på sitemap'en, legg til underelementer
            addToSiteMap(sitemap, tmpentries);
        }

        return sitemap;
    }


    public static SiteMapEntry getSiteMap(int siteId, int depth, int language, AssociationCategory associationCategory, int rootId, int[] currentPath) throws SystemException {
        StringBuilder query = new StringBuilder();

        if (depth != -1) {
            query.append(" and associations.Depth < ").append(depth + 1);
        }
        if (language != -1) {
            query.append(" and contentversion.Language = ").append(language);
        }
        query.append(" and associations.SiteId = ").append(siteId);
        if (associationCategory != null) {
            query.append(" and (associations.Category = 0 or associations.Category = ").append(associationCategory.getId());
            for (int id : currentPath) {
                if (id != -1) {
                    query.append(" or associations.AssociationId = ").append(id);
                }
            }
            if (rootId != -1) {
                query.append(" or associations.AssociationId = ").append(rootId);
  	        }
            query.append(")");

        }

        return getSiteMapBySQL(query, rootId, false, null);
    }

    public static SiteMapEntry getSiteMap(int siteId, int depth, int language, AssociationCategory associationCategory, int rootId, int currentId) throws SystemException {
        int[] currentPath = {currentId};

        return getSiteMap(siteId, depth, language, associationCategory, rootId, currentPath);
    }

    public static SiteMapEntry getPartialSiteMap(int siteId, int[] idList, String sort, boolean showExpired) throws SystemException {
        return SiteMapWorker.getPartialSiteMap(siteId, idList, sort, showExpired, null);
    }

    public static SiteMapEntry getPartialSiteMap(int siteId, int[] idList, String sort, boolean showExpired, int[] associationCategories) throws SystemException {
        StringBuilder query = new StringBuilder();

        query.append(" and associations.SiteId = ").append(siteId);
        query.append(" and associations.ParentAssociationId in (0");
        if (idList != null) {
            for (int id : idList) {
                query.append(",").append(id);
            }
        }
        query.append(")");

        if (associationCategories != null) {
            query.append(" and associations.Category in (0");
            for (int category : associationCategories) {
                query.append(",").append(category);
            }
            query.append(")");
        }

        // Hide the expired pages
        if (!showExpired) {
            query.append(" and content.VisibilityStatus != ").append(ContentVisibilityStatus.ARCHIVED.statusId).append(" and content.VisibilityStatus != ").append(ContentVisibilityStatus.EXPIRED.statusId);
        }

        // Determine if element has children
        SiteMapEntry sitemap = getSiteMapBySQL(query, -1, true, sort);

        if (sitemap != null) {
            // Site map can be null if no pages are created yet
            List<SiteMapEntry> leafNodes = new ArrayList<>();
            getLeafNodes(leafNodes, sitemap);

            updateStatusForLeafNodes(leafNodes, associationCategories);
        }

        return sitemap;
    }

    private static void updateStatusForLeafNodes(List<SiteMapEntry> leafNodes, int[] associationCategories) {
        StringBuilder query = new StringBuilder("select ParentAssociationId from associations where associations.ParentAssociationId in (");

        for (int i = 0; i < leafNodes.size(); i++) {
            SiteMapEntry leafNode = leafNodes.get(i);
            if (i > 0) {
                query.append(",");
            }
            query.append(leafNode.currentId);
        }
        query.append(")");

        if (associationCategories != null) {
            query.append(" and associations.Category in (0");
            for (int category : associationCategories) {
                query.append(",").append(category);
            }
            query.append(")");
        }

        try (Connection c = dbConnectionFactory.getConnection()){
            ResultSet rs = SQLHelper.getResultSet(c, query.toString());
            while(rs.next()) {
                int id = rs.getInt("ParentAssociationId");
                for (SiteMapEntry leafNode : leafNodes) {
                    if (leafNode.currentId == id) {
                        leafNode.setHasChildren(true);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }

    private static void getLeafNodes(List<SiteMapEntry> leafNodes, SiteMapEntry sitemap) {
        List children = sitemap.getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                getLeafNodes(leafNodes, (SiteMapEntry)children.get(i));
            }
        } else {
            leafNodes.add(sitemap);
        }
    }


    public static SiteMapEntry getPartialSiteMap(Content content, AssociationCategory associationCategory, boolean useLocalMenus, boolean getAll) throws SystemException {
        StringBuilder query = new StringBuilder();

        if (content == null) {
            return null;
        }

        int siteId = -1;

        int pathIds[] = null;
        Association association = content.getAssociation();
        if (association != null) {
            siteId = association.getSiteId();
            if (association.getPath().length() > 0) {
                String path = "/0" + association.getPath() + association.getId() + "/";
                pathIds = StringHelper.getInts(path, "/");
            }
        }

        // Kan ha mulighet for å bruke lokalmeny
        int rootId = -1;
        int pathStartOffset = 0;

        if (siteId != -1) {
            query.append(" and associations.SiteId = ").append(siteId);
        }

        if (pathIds != null) {
            query.append(" and ((associations.ParentAssociationId in (");
            if (useLocalMenus) {
                // Ved lokale menyer skal kun endel av pathen returneres
                List associations = AssociationAO.getAssociationsByContentId(content.getGroupId());
                for (int i = 0; i < pathIds.length; i++) {
                    for (int j = 0; j < associations.size(); j++) {
                        Association a =  (Association)associations.get(j);
                        if (pathIds[i] == a.getAssociationId()) {
                            pathStartOffset = i;
                            if (i > 0) {
                                rootId = pathIds[i];
                            }
                            break;
                        }
                    }
                }
            }

            for (int i = pathStartOffset; i < pathIds.length; i++) {
                if (i > pathStartOffset) {
                    query.append(",");
                }
                query.append(pathIds[i]);
            }

            if (associationCategory != null) {
                query.append(") and (associations.Category = 0 or associations.Category = ").append(associationCategory.getId()).append("))");
            } else {
                query.append("))");
            }

            query.append(" or associations.AssociationId in (");
            for (int i = pathStartOffset; i < pathIds.length; i++) {
                if (i > pathStartOffset) {
                    query.append(",");
                }
                query.append(pathIds[i]);
            }
            query.append("))");
        }

        return getSiteMapBySQL(query, rootId, getAll, null);
    }
}
