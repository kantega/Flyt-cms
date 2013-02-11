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
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PathWorker {

    private static final String SOURCE = "aksess.PathWorker";


    public static List<PathEntry> getPathByAssociation(Association association) throws SystemException {
        List<PathEntry> pathEntries = new ArrayList<PathEntry>();

        int pathIds[] = association.getPathElementIds();
        if (pathIds == null || pathIds.length == 0) {
            return pathEntries;
        }

        String strIds = "";

        // Legg inn alle element fra path i rekkefølge
        for (int i = 0; i < pathIds.length; i++) {
            PathEntry entry = new PathEntry(pathIds[i], "");
            pathEntries.add(entry);
            if (i > 0) {
                strIds += ",";
            }
            strIds += pathIds[i];
        }

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, "select contentversion.Title, content.ContentTemplateId, associations.AssociationId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (" + strIds + ")");
            while(rs.next()) {
                String title = rs.getString("Title");
                int id = rs.getInt("AssociationId");
                int contentTemplateId = rs.getInt("ContentTemplateId");
                for (PathEntry entry : pathEntries) {
                    if (entry.getId() == id) {
                        entry.setTitle(title);
                        entry.setContentTemplateId(contentTemplateId);
                        break;
                    }
                }
            }
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

        return pathEntries;
    }


    public static List<PathEntry> getPathByContentId(ContentIdentifier cid) throws SystemException {
        List<PathEntry> pathEntries = new ArrayList<PathEntry>();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            if (cid == null) {
                return pathEntries;
            }

            ResultSet rs = SQLHelper.getResultSet(c, "select Path from associations where UniqueId = " + cid.getAssociationId());
            if (!rs.next()) {
                return pathEntries;
            }
            String path = rs.getString("Path");
            rs.close();

            int pathIds[] = StringHelper.getInts(path, "/");
            if (pathIds.length == 0) {
                return pathEntries;
            }

            String strIds = "";

            // Legg inn alle element fra path i rekkefølge
            for (int i = 0; i < pathIds.length; i++) {
                PathEntry entry = new PathEntry(pathIds[i], "");
                pathEntries.add(entry);
                if (i > 0) {
                    strIds += ",";
                }
                strIds += pathIds[i];
            }

            rs = SQLHelper.getResultSet(c, "select contentversion.Title, associations.AssociationId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (" + strIds + ")");
            while(rs.next()) {
                String title = rs.getString("Title");
                int id = rs.getInt("AssociationId");

                for (PathEntry entry : pathEntries) {
                    if (entry.getId() == id) {
                        entry.setTitle(title);
                        break;
                    }
                }
            }
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

        return pathEntries;
    }


    public static List<PathEntry> getMultimediaPath(Multimedia mm) throws SystemException {
        List<PathEntry> pathentries = new ArrayList<PathEntry>();

        Connection c = null;

        int parentId = mm.getParentId();

        try {
            c = dbConnectionFactory.getConnection();
            while (parentId != 0) {
                ResultSet rs = SQLHelper.getResultSet(c, "select Id, ParentId, Name from multimedia where id = " + parentId);
                if(rs.next()) {
                    int id = rs.getInt("Id");
                    parentId = rs.getInt("ParentId");
                    String title = rs.getString("Name");
                    PathEntry entry = new PathEntry(id, title);
                    pathentries.add(0, entry);
                }
            }
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

        return pathentries;
    }
}
