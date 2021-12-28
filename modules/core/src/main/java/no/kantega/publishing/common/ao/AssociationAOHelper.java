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

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.kantega.publishing.common.ao.AssociationAO.getAssociationById;
import static no.kantega.publishing.common.ao.AssociationAO.getAssociationsByContentId;

class AssociationAOHelper {

    /**
     * Object containing lists that represent the {@code Association}s that will be affected by a modification of a cross published {@code Content}.
     * <ul>
     *     <li><b>associationsToMove</b>: {@code Association}s we have found a new parent for, and have set parentId on.</li>
     *     <li><b>associationsToDelete</b>: {@code Association}s we did not find a new parent for.</li>
     *     <li><b>parentAssociationsNeedingNewChild</b>: Cross published versions of the new parent we do not have a existing
     *     {@code Association} for, so a new {@code Association} will have to be created.</li>
     * </ul>
     */
    static class MoveCrossPublishedResult {
        final List<Association> associationsToMove;
        final List<Association> associationsToDelete;
        final List<Association> parentAssociationsNeedingNewChild;

        MoveCrossPublishedResult(List<Association> associationsToMove, List<Association> associationsToDelete, List<Association> parentAssociationsNeedingNewChild) {
            this.associationsToMove = associationsToMove;
            this.associationsToDelete = associationsToDelete;
            this.parentAssociationsNeedingNewChild = parentAssociationsNeedingNewChild;
        }
    }

    /**
     *
     * @param oldAssociation - the current {@code Association} that is being modified.
     * @param interestingAssociations - all other cross published {@code Association}s refering to {@code oldAssociation.contentId}
     * @param newAssociation - the updated version that in the future will be saved.
     * @return {@code MoveCrossPublishedResult} containing lists of objects that will be moved, deleted or created new {@code Association}s under.
     */
    static MoveCrossPublishedResult handleMoveCrossPublished(Association oldAssociation, List<Association> interestingAssociations, Association newAssociation){
        List<Association> associationsToMove = new ArrayList<>();
        List<Association> associationsToDelete = new ArrayList<>();
        List<Association> parentAssociationsNeedingNewChild = new ArrayList<>();

        Association firstSharedAnchestor = getFirstSharedAnchestor(oldAssociation, interestingAssociations.get(0));

        if(firstSharedAnchestor != null){
            final Association newParentForInitiatingAssociation = getAssociationById(newAssociation.getParentAssociationId());
            List<Association> interestingNewParents =
                    getAssociationsByContentId(newParentForInitiatingAssociation.getContentId()).stream()
                            .filter(a -> a.getId() != newParentForInitiatingAssociation.getId())
                            .collect(Collectors.toList());
            for (Association interestingAssociation : interestingAssociations) {
                Association newParent = findNewParent(interestingAssociation, interestingNewParents);
                if (newParent != null) {
                    interestingAssociation.setParentAssociationId(newParent.getAssociationId());
                    associationsToMove.add(interestingAssociation);
                } else {
                    associationsToDelete.add(interestingAssociation);
                }
            }

            parentAssociationsNeedingNewChild = interestingNewParents;
        }
        return new MoveCrossPublishedResult(associationsToMove, associationsToDelete, parentAssociationsNeedingNewChild);
    }

    /**
     * @param interestingAssociation the {@code Association} we want to find new parent for.
     * @param interestingNewParents the potential parents. The returned parent should be removed from the list.
     * @return the new parent {@code Association}.
     * Currently just selects the first of interestingNewParents and remove it from interestingNewParents
     */
    private static Association findNewParent(Association interestingAssociation, List<Association> interestingNewParents) {
        if(interestingNewParents.size() > 0){
            return interestingNewParents.remove(0);
        }
        return null;
    }

    /**
     * @param oldAssociation the association that initiated the operation
     * @param association the first sibling association that is not oldAssociation.
     * @return the first shared anchestor of both associations. I.e. the parent of association
     * if association.parent.contentId == oldAssociation.parent.contentId. null otherwise
     *
     */
    private static Association getFirstSharedAnchestor(Association oldAssociation, Association association) {
        Association parent = getAssociationById(oldAssociation.getParentAssociationId());
        Association parentCopy = getAssociationById(association.getParentAssociationId());

        return parent.getContentId() == parentCopy.getContentId() ? parentCopy : null;
    }

    public static void fixDefaultPostings() throws SystemException {
        List<Site> sites = RootContext.getInstance().getBean(SiteCache.class).getSites();
        // MySQL støtter ikke å oppdatere tabeller som er med i subqueries, derfor denne tungvinte måten å gjøre det på
        String query = "SELECT min(uniqueid) from associations WHERE siteid = ? AND type = " + AssociationType.CROSS_POSTING + " AND (IsDeleted IS NULL OR IsDeleted = 0) AND contentid NOT IN " +
                " (SELECT contentid from associations WHERE siteid = ? AND type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " AND (IsDeleted IS NULL OR IsDeleted = 0)) GROUP BY contentid";
        String updateQuery = "UPDATE associations SET type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " WHERE uniqueid = ? AND (IsDeleted IS NULL OR IsDeleted = 0)";

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement(query);
             PreparedStatement updateSt = c.prepareStatement(updateQuery)) {

            for (Site site : sites) {
                st.setInt(1, site.getId());
                st.setInt(2, site.getId());
                try(ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        updateSt.setInt(1, id);
                        updateSt.addBatch();
                    }
                    updateSt.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }


    public static void deleteShortcuts() throws SystemException {
        try (Connection c = dbConnectionFactory.getConnection()) {

            if (dbConnectionFactory.isMySQL()) {
                // MySQL støtter ikke å slette tabeller som er med i subqueries, derfor denne tungvinte måten å gjøre det på
                String query = "SELECT UniqueId FROM associations WHERE type = " + AssociationType.SHORTCUT + " AND AssociationId NOT IN (SELECT UniqueId FROM associations WHERE (IsDeleted IS NULL OR IsDeleted = 0)) AND (IsDeleted IS NULL OR IsDeleted = 0)";
                String updateQuery = "DELETE FROM associations WHERE UniqueId = ?";
                try(PreparedStatement st = c.prepareStatement(query);
                PreparedStatement updateSt = c.prepareStatement(updateQuery)) {

                    ResultSet rs = st.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        updateSt.setInt(1, id);
                        updateSt.addBatch();
                    }
                    updateSt.executeBatch();
                }
            } else {
                try(PreparedStatement st = c.prepareStatement("DELETE FROM associations WHERE type = " + AssociationType.SHORTCUT + " AND AssociationId NOT IN (SELECT UniqueId FROM associations WHERE (IsDeleted IS NULL OR IsDeleted = 0)) AND (IsDeleted IS NULL OR IsDeleted = 0)")) {
                    st.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }
}
