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

package no.kantega.publishing.common;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssociationHelper {

    public static List<Association> createAssociationsFromParentIds(int[] parentIds) throws SystemException {
        List<Association> associations = new ArrayList<Association>();

        List<Integer> sites = new ArrayList<Integer>();

        for (int parentId : parentIds) {
            Association parent = AssociationAO.getAssociationById(parentId);

            Association association = new Association();
            association.setContentId(-1);
            association.setParentAssociationId(parent.getAssociationId());
            association.setSiteId(parent.getSiteId());
            association.setSecurityId(parent.getSecurityId());

            boolean found = sites.contains(association.getSiteId());

            if (!found) {
                sites.add(association.getSiteId());
            }

            association.setAssociationtype(parent.getAssociationtype());

            String path = getPathForId(parent.getAssociationId());
            association.setPath(path);

            // Finn dybde
            int depth = 0;
            if (path.length() > 1) {
                for (int j = 1; j < path.length(); j++) {
                    char c = path.charAt(j);
                    if (c == '/') depth++;
                }
            }
            association.setDepth(depth);

            associations.add(association);
        }

        // Sjekk at det er kun en hovedknytning per site og at det er en per site
        for (Integer siteId : sites) {
            int noDefaultPostings = 0;
            int first = -1;

            for (int j = 0; j < associations.size(); j++) {
                Association tmpA = associations.get(j);
                if (siteId == tmpA.getSiteId()) {
                    if (first == -1) first = j;
                    if (tmpA.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                        noDefaultPostings++;
                        if (noDefaultPostings > 1) {
                            tmpA.setAssociationtype(AssociationType.CROSS_POSTING);
                        }
                    }
                }
            }

            // Ingen default posting, bruk den første
            if (noDefaultPostings == 0 && first != -1) {
                Association tmpA = associations.get(first);
                tmpA.setAssociationtype(AssociationType.DEFAULT_POSTING_FOR_SITE);
            }
        }

        return associations;
    }

    public static String getPathForId(int id) throws SystemException {
        Connection c = null;
        String path = "";

        try {
            c = dbConnectionFactory.getConnection();

            path = getPathForId(c, path, id) + "/";
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                // Could not close connection, probably closed already
            }
        }

        return path;
    }

    private static String getPathForId(Connection c, String path, int id) throws SystemException {
        try {
            PreparedStatement st = c.prepareStatement("select ParentAssociationId from associations where AssociationId = ?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int cid = rs.getInt("ParentAssociationId");
                path = getPathForId(c, path, cid);
                path = path + "/" + id;
            } else {
                return path;
            }
            st.close();
            rs.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return path;
    }

}
