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
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SiteMapWorker {
    private static final String SOURCE = "aksess.SiteMapWorker";

    private static SiteMapEntry getFirst(int parentId, List entries) {
        for (int i = 0; i < entries.size(); i++) {
            SiteMapEntry e = (SiteMapEntry)entries.get(i);
            // Snarveier kan aldri være parents
            if (e.parentId == parentId) {
                entries.remove(e);
                return e;
            }
        }
        return null;
    }

    private static void addToSiteMap(SiteMapEntry parent, List entries) {
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


    public static SiteMapEntry getSiteMapBySQL(StringBuffer where, int rootId, boolean getAll, String sort) throws SystemException {
        List tmpentries = new ArrayList();

        StringBuffer query = new StringBuffer();
        query.append("select content.ContentId, content.Type, content.Alias, content.VisibilityStatus, content.NumberOfNotes, content.Location, content.OpenInNewWindow, content.Owner, content.OwnerPerson, contentversion.Status, contentversion.Title, contentversion.LastModified, associations.UniqueId, associations.AssociationId, associations.ParentAssociationId, associations.Type, associations.Category, associations.SecurityId, content.GroupId from content, contentversion, associations where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.ContentId = associations.ContentId and (associations.IsDeleted IS NULL OR associations.IsDeleted = 0)");
        query.append(where);
        if (!getAll) {
            query.append(" and contentversion.Status = " + ContentStatus.PUBLISHED);
            query.append(" and (content.VisibilityStatus = " + ContentVisibilityStatus.ACTIVE + ")");
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

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
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
                int status  = rs.getInt(p++);
                String title = rs.getString(p++);
                Date lastModified = rs.getDate(p++);

                int uniqueId = rs.getInt(p++);
                int currentId = rs.getInt(p++);
                int parentId = rs.getInt(p++);
                int aType = rs.getInt(p++);
                int aCategory = rs.getInt(p++);
                int aSecId = rs.getInt(p++);
                int groupId = rs.getInt(p++);
                if (aType == AssociationType.SHORTCUT) {
                    type = ContentType.SHORTCUT;
                }
                if ((rootId == -1 && parentId == 0 && type != ContentType.SHORTCUT) || (rootId == uniqueId && type != ContentType.SHORTCUT)) {
                    sitemap = new SiteMapEntry(uniqueId, currentId, parentId, type, status, visibilityStatus, title, numberOfNotes);
                    if (alias != null && alias.length() > 0) {
                        sitemap.setAlias(alias);
                    }
                    sitemap.setAssociationCategory(aCategory);
  	                sitemap.setGroupId(groupId);
                    sitemap.setContentId(contentId);
                    sitemap.setLastModified(lastModified);
                    sitemap.setOwner(owner);
                    sitemap.setOwnerPerson(ownerPerson);
                    sitemap.setSecurityId(aSecId);
                } else {
                    SiteMapEntry entry = new SiteMapEntry(uniqueId, currentId, parentId, type, status, visibilityStatus, title, numberOfNotes);
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
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        if (sitemap != null) {
            // Vi har funnet starten på sitemap'en, legg til underelementer
            addToSiteMap(sitemap, tmpentries);
        }

        return sitemap;
    }


    public static SiteMapEntry getSiteMap(int siteId, int depth, int language, AssociationCategory associationCategory, int rootId, int currentId) throws SystemException {
        StringBuffer query = new StringBuffer();

        if (depth != -1) {
            query.append(" and associations.Depth < " + (depth+1));
        }
        if (language != -1) {
            query.append(" and contentversion.Language = " + language);
        }
        query.append(" and associations.SiteId = " + siteId);
        if (associationCategory != null) {
            query.append(" and (associations.Category = 0 or associations.Category = " + associationCategory.getId());
            if (currentId != -1) {
                query.append(" or associations.AssociationId = " + currentId);
            }
            if (rootId != -1) {
  	            query.append(" or associations.AssociationId = " + rootId);
  	        }
            query.append(")");

        }

        return getSiteMapBySQL(query, rootId, false, null);
    }

    public static SiteMapEntry getPartialSiteMap(int siteId, int[] idList, int language, boolean getAll, String sort) throws SystemException {
        StringBuffer query = new StringBuffer();

        if (language != -1) {
            query.append(" and contentversion.Language = " + language);
        }
        query.append(" and associations.SiteId = " + siteId);
        query.append(" and associations.ParentAssociationId in (0");
        if (idList != null) {
            for (int i = 0; i < idList.length; i++) {
                query.append("," + idList[i]);
            }
        }
        query.append(")");

        return getSiteMapBySQL(query, -1, getAll, sort);
    }

    public static SiteMapEntry getPartialSiteMap(Content content, AssociationCategory associationCategory, boolean useLocalMenus, boolean getAll) throws SystemException {
        StringBuffer query = new StringBuffer();

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
            query.append(" and associations.SiteId = " + siteId);
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
                query.append(") and (associations.Category = 0 or associations.Category = " + associationCategory.getId() + "))");
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

    /**
     *  Metoder for testing, skriver ut sitemap over hele nettstedet...
     */

    private static void printSiteMap(SiteMapEntry sitemap, int depth) {
        if (sitemap != null) {
            for (int i = 0; i < depth; i++) {
                System.out.print("---");
            }
            System.out.print(sitemap.title + "(" + sitemap.currentId + ")");
            System.out.print("\n");
            List children = sitemap.getChildren();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    printSiteMap((SiteMapEntry)children.get(i), depth + 1);
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        SiteMapEntry entry = getPartialSiteMap(1, new int[] {1,32,45,46,47}, 0, true, null);

        printSiteMap(entry, 0);
    }
}
