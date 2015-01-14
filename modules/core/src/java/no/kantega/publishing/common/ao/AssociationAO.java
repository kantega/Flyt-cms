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
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.common.AssociationHelper;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DeletedItem;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssociationAO  {
    private static final Logger log = LoggerFactory.getLogger(AssociationAO.class);

    private static PermissionAO permissionAO;

    public static Association getAssociationFromRS(ResultSet rs) throws SQLException {
        Association association = new Association();
        association.setId(rs.getInt("UniqueId"));
        association.setAssociationId(rs.getInt("AssociationId"));
        association.setContentId(rs.getInt("ContentId"));
        association.setParentAssociationId(rs.getInt("ParentAssociationId"));
        association.setCategory(new AssociationCategory(rs.getInt("Category")));
        association.setSiteId(rs.getInt("SiteId"));
        association.setSecurityId(rs.getInt("SecurityId"));
        association.setAssociationtype(rs.getInt("Type"));
        association.setPriority(rs.getInt("Priority"));
        association.setPath(rs.getString("Path"));
        association.setDepth(rs.getInt("Depth"));
        association.setDeleted(rs.getInt("IsDeleted") == 1);
        association.setNumberOfViews(rs.getInt("NumberOfViews"));
        return association;
    }

    public static Association getAssociationById(int id) throws SystemException {
        Association a = null;

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement("select * from associations where uniqueid = ?")){
            st.setInt(1, id);
            try(ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    a = getAssociationFromRS(rs);
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return a;
    }


    private static void addAssociation(Connection c, Association a) throws SystemException, SQLException {
        int aid = a.getParentAssociationId();

        // Finn path
        String path = AssociationHelper.getPathForId(aid);
        a.setPath(path);

        // Finn dybde
        int depth = 0;
        if (path.length() > 1) {
            for (int i = 1; i < path.length(); i++) {
                if (path.charAt(i) == '/') depth++;
            }
        }
        a.setDepth(depth);

        // Arv rettigheter fra parent dersom ikke satt
        if (a.getSecurityId() == -1 && a.getParentAssociationId() > 0) {
            int secId = SQLHelper.getInt(c, "select SecurityId from associations where UniqueId = ?", "SecurityId", new Object[]{a.getParentAssociationId()});
            a.setSecurityId(secId);
        }

        if (a.getAssociationtype() != AssociationType.SHORTCUT) {
            // Avgjør om dette er en krysspublisering eller ikke
            try(ResultSet rs = SQLHelper.getResultSet(c, "select UniqueId from associations where ContentId = ? and SiteId = ?", new Object[]{a.getContentId(), a.getSiteId()})) {
                if (rs.next()) {
                    a.setAssociationtype(AssociationType.CROSS_POSTING);
                } else {
                    a.setAssociationtype(AssociationType.DEFAULT_POSTING_FOR_SITE);
                }
            }
        }

        try(PreparedStatement st = c.prepareStatement("insert into associations (AssociationId, ContentId, ParentAssociationId, Category, SiteId, SecurityId, Type, Priority, Path, Depth, IsDeleted, DeletedItemsId, NumberOfViews) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {"UNIQUEID"})) {
            st.setInt(1, a.getAssociationId());
            st.setInt(2, a.getContentId());
            st.setInt(3, a.getParentAssociationId());
            st.setInt(4, a.getCategory().getId());
            st.setInt(5, a.getSiteId());
            st.setInt(6, a.getSecurityId());
            st.setInt(7, a.getAssociationtype());
            st.setLong(8, a.getPriority());
            st.setString(9, a.getPath());
            st.setInt(10, a.getDepth());
            st.setInt(11, 0);
            st.setInt(12, 0);
            st.setInt(13, 0);
            st.execute();

            try(ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                } else {
                    log.error("Feilet ved uthenting av nøkkel - id");
                }
            }
        }

        // For alt annet enn snarveier er associationid == id
        if (a.getAssociationId() == -1) {
            a.setAssociationId(a.getId());
            try(PreparedStatement st = c.prepareStatement("update associations set AssociationId = ? where uniqueid = ?")) {
                st.setInt(1, a.getId());
                st.setInt(2, a.getId());
                st.execute();
            }
        }

        // Sett defaultrettigheter pø startside, alle kan gjøre alt
        if (a.getSecurityId() == -1 && a.getParentAssociationId() == 0) {
            if(permissionAO == null) permissionAO = RootContext.getInstance().getBean(PermissionAO.class);
            permissionAO.setPermissions(a, null);
            a.setSecurityId(a.getId());
        }
    }

    /**
     * Legg til kopling for side
     * @param association - kopling
     * @throws SystemException
     */
    public static void addAssociation(Association association) throws SystemException {

        if (association.getAssociationtype() == AssociationType.SHORTCUT && association.getAssociationId() == -1) {
            // Kan ikke opprette en snarvei uten å ha en knytningsid
            return;
        }


        try (Connection c = dbConnectionFactory.getConnection()){
            addAssociation(c, association);
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }

    private static Association copyAssociations(Connection c , Association source, Association target, AssociationCategory category, boolean copyChildren) throws SQLException, SystemException {

        Association newAssociation = new Association();

        newAssociation.setSiteId(target.getSiteId());
        newAssociation.setContentId(source.getContentId());
        newAssociation.setSecurityId(target.getSecurityId());
        newAssociation.setPriority(source.getPriority());
        newAssociation.setParentAssociationId(target.getId());
        newAssociation.setAssociationtype(source.getAssociationtype());
        if (category != null) {
            newAssociation.setCategory(category);
        } else {
            newAssociation.setCategory(source.getCategory());
        }

        addAssociation(c, newAssociation);

        if (copyChildren) {
            // Finn barn til source legg dem inn
           try(ResultSet rs = SQLHelper.getResultSet(c, "select * from associations where ParentAssociationId = ? AND (IsDeleted IS NULL OR IsDeleted = 0)", new Object[]{source.getId()})) {
               while (rs.next()) {
                   Association child = getAssociationFromRS(rs);
                   copyAssociations(c, child, newAssociation, null, copyChildren);
               }
           }
        }

        return newAssociation;
    }

    /**
     * Krysspubliserer en side med undersider
     * @param source - side som skal krysspubliseres
     * @param target - hvor den skal krysspublisers under
     * @param category - spalte den skal krysspubliseres til
     * @param copyChildren - skal undersider krysspubliseres
     * @return - oppdatert kopling
     * @throws SystemException - Systemfeil
     */
    public static Association copyAssociations(Association source, Association target, AssociationCategory category, boolean copyChildren) throws SystemException {

        if (source.getAssociationtype() == AssociationType.SHORTCUT) {
            // Snarveier kan ikke kopieres
            return null;
        }

        try (Connection c = dbConnectionFactory.getConnection()){

            return copyAssociations(c, source, target, category, copyChildren);
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }


    public static List<Association> getAssociationsByContentId(int contentId) throws SystemException {
        List<Association> associations = new ArrayList<>();

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement("select * from associations where contentid = ?")) {
            st.setInt(1, contentId);
            try (ResultSet rs = st.executeQuery()){
                while (rs.next()) {
                    associations.add(getAssociationFromRS(rs));
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
        return associations;
    }


    public static List<Association> getAssociationsByContentIdAndParentId(int contentId, int parentId) throws SystemException {
        List<Association> associations = new ArrayList<>();

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement("select * from associations where contentid = ? and path like ?")){
            st.setInt(1, contentId);
            st.setString(2, "%/" + parentId + "/%");
            try(ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    associations.add(getAssociationFromRS(rs));
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return associations;
    }


    /**
     * Flytter en struktur
     * @param newAssociation - oppdatert kopling
     * @param updateCopies - dersom siden er kopiert (krysspublisert), angi om krysspubliserte sider ogsø skal flyttes
     * @throws SystemException - Systemfeil
     */
    public static void modifyAssociation(Association newAssociation, boolean updateCopies, boolean updateGroup) throws SystemException {
        if (updateCopies) {
            // Hent fra basen kopling i nøvørende form og parent
            Association oldAssociation = getAssociationById(newAssociation.getId());
            if (oldAssociation.getParentAssociationId() > 0) {
                // Siden som skal flyttes kan vøre krysspublisert

                int contentId = newAssociation.getContentId();

                List<Association> associations = getAssociationsByContentId(contentId);
                for (Association copy : associations) {
                    // Finn om dette er krysspublisert og finn startpunkt for krysspublisering

                    if (newAssociation.getId() != copy.getId()) {
                        int path[] = oldAssociation.getPathElementIds();
                        int pathCopy[] = copy.getPathElementIds();

                        Association startCrossPublished = null;

                        // Finnes felles startpunkt for krysspublisering av denne kopien med den som flyttes
                        if (copy.getId() != newAssociation.getId() && path.length > 1 && pathCopy.length > 1) {
                            for (int j = 0; j < path.length && j < pathCopy.length; j++) {
                                int parentId = path[(path.length - 1) - j];
                                int parentIdCurrent = pathCopy[(pathCopy.length - 1) - j];

                                Association parent = getAssociationById(parentId);
                                Association parentCopy = getAssociationById(parentIdCurrent);
                                if (parent.getContentId() != parentCopy.getContentId() || parent.getId() == parentCopy.getId()) {
                                    // Vi har kommet til en side som ikke er krysspublisert eller til samme id, stopp
                                    break;
                                }

                                startCrossPublished = parentCopy;
                            }

                        }
// startCrossPublished = en krysspublisert versjon av gammel parent
                        if (startCrossPublished != null) {
                            // Sjekk under startpunktet om vi finner side med samme contentid som den som skal flyttes

                            List<Association> tmp = getAssociationsByContentIdAndParentId(contentId, startCrossPublished.getId());
                            if (tmp.size() == 1) {
                                Association newAssociationCopy = tmp.get(0); // krysspublisert versjon av oldAssociation, vi må flytte denne også.
                                Association newParent = getAssociationById(newAssociation.getParentAssociationId());
                                List<Association> newParentAssociations = getAssociationsByContentId(newParent.getContentId());
                                for (Association newParentAssociation : newParentAssociations) {
                                    if(newParentAssociation.getId() != newParent.getId()){
                                        newAssociationCopy.setParentAssociationId(newParentAssociation.getId());
                                        modifyAssociation(newAssociationCopy, updateGroup);
                                    }
                                }

                            }
                        }
                    }

                }

            }
        }
        modifyAssociation(newAssociation, updateGroup);
    }

    private static void modifyAssociation(Association newAssociation, boolean updateGroupId) throws SystemException {

        Association oldAssocation = getAssociationById(newAssociation.getId());


        // Finn path og lagre denne
        String path = AssociationHelper.getPathForId(newAssociation.getParentAssociationId());
        newAssociation.setPath(path);

        // Finn dybde
        int depth = 0;
        if (path.length() > 1) {
            for (int i = 1; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c == '/') depth++;
            }
        }
        newAssociation.setDepth(depth);

        try (Connection c = dbConnectionFactory.getConnection()){
            int currentGroupId = SQLHelper.getInt(c, "select GroupId from content where ContentId = ?", "GroupId", new Object[]{oldAssocation.getContentId()});
            int parentContentId = SQLHelper.getInt(c, "select ContentId from associations where UniqueId = ?", "ContentId", new Object[]{newAssociation.getParentAssociationId()});
            int parentGroupId  = SQLHelper.getInt(c, "select GroupId from content where ContentId = ?", "GroupId", new Object[]{parentContentId});
            int parentSecurityId = SQLHelper.getInt(c, "select SecurityId from associations where UniqueId = ?", "SecurityId", new Object[]{newAssociation.getParentAssociationId()});

            if (currentGroupId == oldAssocation.getContentId() || currentGroupId == parentGroupId) {
                updateGroupId = false;
            }

            boolean updateSecurityId = true;
            if (oldAssocation.getSecurityId() == parentSecurityId || oldAssocation.getSecurityId() == oldAssocation.getId()) {
                updateSecurityId = false;
            }

            // Oppdater hovedknytning
            try(PreparedStatement pathst = c.prepareStatement("update associations set Category = ?, Path = ?, Depth = ?, SiteId = ?, ParentAssociationId = ? where UniqueId = ?")) {
                pathst.setInt(1, newAssociation.getCategory().getId());
                pathst.setString(2, path);
                pathst.setInt(3, depth);
                pathst.setInt(4, newAssociation.getSiteId());
                pathst.setInt(5, newAssociation.getParentAssociationId());
                pathst.setInt(6, newAssociation.getId());
                pathst.execute();
            }
            // Oppdater gruppe for denne siden
            if (updateGroupId) {
                try(PreparedStatement groupst = c.prepareStatement("update content set GroupId = ? where ContentId = ?")) {
                    groupst.setInt(1, parentGroupId);
                    groupst.setInt(2, oldAssocation.getContentId());
                    groupst.execute();
                }
            }

            // Oppdater gruppe for denne siden
            if (updateSecurityId) {
                try(PreparedStatement securityst = c.prepareStatement("update associations set SecurityId = ? where UniqueId = ?")) {
                    securityst.setInt(1, parentSecurityId);
                    securityst.setInt(2, newAssociation.getId());
                    securityst.execute();
                }
            }

            // Finn alle som ligger under og oppdater path og groupid
            try(ResultSet rs = SQLHelper.getResultSet(c, "select * from associations where Path like ?", new Object[]{"%/" + oldAssocation.getId() +  "/%"});
                PreparedStatement pathst  = c.prepareStatement("update associations set Path = ?, Depth = ?, SiteId = ? where UniqueId = ?");
                PreparedStatement groupst = c.prepareStatement("update content set GroupId = ? where ContentId = ? and GroupId = ?");
                PreparedStatement securityst = c.prepareStatement("update associations set SecurityId = ? where UniqueId = ? and SecurityId = ?")) {
                while (rs.next()) {
                    int tmpCid = rs.getInt("ContentId");
                    int tmpId = rs.getInt("UniqueId");
                    int tmpType = rs.getInt("Type");
                    String tmpPath = rs.getString("Path");

                    // Lag ny path
                    String newPath = path.substring(0, path.length() - 1) + tmpPath.substring(tmpPath.indexOf("/" + oldAssocation.getId() + "/"), tmpPath.length());

                    // Finn dybde
                    int newDepth = 0;
                    if (newPath.length() > 1) {
                        for (int i = 1; i < newPath.length(); i++) {
                            char tmpc = newPath.charAt(i);
                            if (tmpc == '/') newDepth++;
                        }
                    }

                    // Oppdater path og dybde
                    pathst.setString(1, newPath);
                    pathst.setInt(2, newDepth);
                    pathst.setInt(3, newAssociation.getSiteId());
                    pathst.setInt(4, tmpId);
                    pathst.execute();

                    // Oppdater gruppeid for barn
                    if (updateGroupId && tmpType == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                        groupst.setInt(1, parentGroupId);
                        groupst.setInt(2, tmpCid);
                        groupst.setInt(3, currentGroupId);
                        groupst.execute();
                    }

                    if (updateSecurityId) {
                        securityst.setInt(1, parentSecurityId);
                        securityst.setInt(2, tmpId);
                        securityst.setInt(3, oldAssocation.getSecurityId());
                        securityst.execute();
                    }

                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }


    private static void appendPathSql(StringBuilder where, List<Association> associations, String op, String not) {
        where.append("(");
        for (int i = 0; i < associations.size(); i++) {
            Integer id = associations.get(i).getId();
            if (i > 0) {
                where.append(op);
            }
            where.append(" path ").append(not).append(" LIKE '%/").append(id.intValue()).append("/%' ");
        }
        where.append(")");
    }


    private static void appendAssociationsSql(StringBuilder where, List<Association> associations) {
        where.append("(");
        for (int i = 0; i < associations.size(); i++) {
            Integer id = associations.get(i).getId();
            if (i > 0) {
                where.append(",");
            }
            where.append(id.intValue());
        }
        where.append(")");
    }

    /**
     * @param associations - that we want to delete. The implementation assumes these point to the same contentId
     * @param deleteMultiple - Do we accept that subpages should be deleted? If false no content will be deleted when
     *                       the content has sub pages.
     * @param userId - User that is deleting.
     * @return list with the content deleted. These content will contain only contentId and title.
     * @throws SystemException
     */
    public static List<Content> deleteAssociationsById(List<Association> associations, boolean deleteMultiple, String userId) throws SystemException {

        List<Content> deletedContent = new ArrayList<>();

        if (associations == null || associations.size() == 0) {
            return deletedContent;
        }

        try (Connection c = dbConnectionFactory.getConnection()) {

            /*
             * Sjekk om denne siden vil bli slettet
             */
            Content current = null;

            try (PreparedStatement st = c.prepareStatement("select ContentId, Title FROM contentversion WHERE ContentId IN" +
                    "(SELECT ContentId From associations WHERE UniqueId = ?) AND contentversion.IsActive = 1")){
            st.setInt(1, associations.get(0).getId());
            try(ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    current = new Content();
                    current.setId(rs.getInt("ContentId"));
                    current.setTitle(rs.getString("Title"));
                }
            }
            if (current == null) {
                return deletedContent;
            }

            }

            StringBuilder query = new StringBuilder();

            query.append("SELECT COUNT(ContentId) AS Cnt FROM associations WHERE ContentId = ");
            query.append(current.getId());
            query.append(" AND Type <> ");
            query.append(AssociationType.SHORTCUT);
            query.append(" AND (IsDeleted IS NULL OR IsDeleted = 0) ");
            query.append(" AND UniqueId NOT IN ");
            appendAssociationsSql(query, associations);

            int cnt = SQLHelper.getInt(c, query.toString(), "Cnt");
            if (cnt == 0) {
                deletedContent.add(current);
            }

            /*
             * Finn underobjekter som vil bli slettet
             */
            query = new StringBuilder();

            StringBuilder titleQuery = new StringBuilder();
            titleQuery.append("SELECT ContentId, Title FROM contentversion WHERE ContentId IN (");

            // Query 1
            query.append("SELECT ContentId  FROM associations WHERE Type <> ");
            query.append(AssociationType.SHORTCUT);
            query.append(" AND (IsDeleted IS NULL OR IsDeleted = 0) AND ");
            appendPathSql(query, associations, "OR", "");
            query.append(" AND contentid NOT IN (");

            // Query 2
            query.append(" SELECT contentid FROM associations WHERE Type <> ");
            query.append(AssociationType.SHORTCUT);
            query.append (" AND (IsDeleted IS NULL OR IsDeleted = 0) AND contentid in (");

            // Query 3
            query.append(" SELECT contentid FROM associations WHERE Type <> ");
            query.append(AssociationType.SHORTCUT);
            query.append(" AND (IsDeleted IS NULL OR IsDeleted = 0) AND ");

            appendPathSql(query, associations, "OR", "");

            // End query 3
            query.append(" ) ");
            query.append(" AND ");
            appendPathSql(query, associations, "AND", "NOT");

            // End query 2
            query.append(")");

            int a = 0;
            try(PreparedStatement st = c.prepareStatement(query.toString());
                ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    if (a > 0) {
                        titleQuery.append(",");
                    }
                    titleQuery.append(rs.getInt("ContentId"));
                    a++;
                }

            }

            titleQuery.append(") AND contentversion.IsActive = 1");

            // å hente ut tittel er splittet opp i to operasjoner, fordi det gør så sinnsykt tregt på MySQL enkelte ganger
            if (a > 0) {
                try(PreparedStatement titleSt = c.prepareStatement(titleQuery.toString());
                    ResultSet titleRs = titleSt.executeQuery()) {
                    while (titleRs.next()) {
                        Content tmp = new Content();
                        tmp.setId(titleRs.getInt("ContentId"));
                        tmp.setTitle(titleRs.getString("Title"));
                        deletedContent.add(tmp);
                    }
                }
            }

            if (deleteMultiple || deletedContent.size() <= 1) {

                // Legg inn i slettede elementer slik at bruker kan angre
                DeletedItem item = new DeletedItem();
                item.setObjectType(ObjectType.ASSOCIATION);
                item.setTitle(current.getTitle());
                item.setDeletedBy(userId);
                int deletedItemsId = DeletedItemsAO.addDeletedItem(item);


                // Marker side for sletting
                query = new StringBuilder();
                query.append("UPDATE associations SET IsDeleted = 1 , DeletedItemsId = ").append(deletedItemsId).append(" WHERE UniqueId IN ");
                appendAssociationsSql(query, associations);

                try(PreparedStatement st = c.prepareStatement(query.toString())) {
                    st.executeUpdate();
                }

                // Marker undersider for sletting
                query = new StringBuilder();
                query.append("UPDATE associations SET IsDeleted = 1, DeletedItemsId = ").append(deletedItemsId).append(" WHERE ");
                appendPathSql(query, associations, "OR", "");

                try(PreparedStatement st = c.prepareStatement(query.toString())) {
                    st.executeUpdate();
                }

                // Slett snarveier til ting som er slettet
                AssociationAOHelper.deleteShortcuts();

                // Fiks defaultpostinger
                AssociationAOHelper.fixDefaultPostings();
            }

            return deletedContent;
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
    }


    public static void setAssociationsPriority(List<Association> associations) throws SystemException {
        if (associations != null && associations.size() > 0) {
            try (Connection c = dbConnectionFactory.getConnection();
                 PreparedStatement st = c.prepareStatement("update associations set Priority = ?, Category=? where UniqueId = ?")){
                for (Association association : associations) {

                    st.setInt(1, association.getPriority());
                    st.setInt(2, association.getCategory().getId());
                    st.setInt(3, association.getId());

                    st.execute();
                }
            } catch (SQLException e) {
                throw new SystemException("SQL Feil ved databasekall", e);
            }
        }
    }


    public static void setSecurityId(Connection c, BaseObject object, boolean setFromParent) throws SQLException, SystemException {
        int newSecurityId = object.getId();
        if (setFromParent) {
            Association a = getAssociationById(object.getId());
            if (a != null) {
                int parentId = a.getParentAssociationId();
                Association parent = getAssociationById(parentId);
                if (parent != null) {
                    newSecurityId = parent.getId();
                }
            }
        }

        // Finn alle undersider, sett nye rettigheter
        try(ResultSet rs = SQLHelper.getResultSet(c, "select UniqueId from associations where Path like ?", new Object[]{"%/" + object.getId() +  "/%"});
            PreparedStatement st = c.prepareStatement("update associations set SecurityId = ? where UniqueId = ? and SecurityId = ?")) {

            // Undersider
            while (rs.next()) {
                int aid = rs.getInt("UniqueId");
                st.setInt(1, newSecurityId);
                st.setInt(2, aid);
                st.setInt(3, object.getSecurityId());

                st.execute();
            }

            st.setInt(1, newSecurityId);
            st.setInt(2, object.getId());
            st.setInt(3, object.getSecurityId());
            st.execute();

            object.setSecurityId(newSecurityId);
        }
    }

    /**
     * Restore delete associations (pages)
     * @param deletedItemsId - id from deleteditems (trash can)
     * @return - id of top page restored
     * @throws SystemException
     */
    public static int restoreAssociations(int deletedItemsId) throws SystemException {

        int parentId = -1;

        try (Connection c = dbConnectionFactory.getConnection();
             PreparedStatement st = c.prepareStatement("SELECT * FROM associations WHERE IsDeleted = 1 AND DeletedItemsId = ? ORDER BY Depth");
             PreparedStatement countSt = c.prepareStatement("SELECT Count(*) AS Cnt FROM associations WHERE IsDeleted = 0 AND ContentId = ? AND SiteId = ?");
             PreparedStatement updateSt = c.prepareStatement("UPDATE associations SET IsDeleted = 0, DeletedItemsId = null, Type = ? WHERE UniqueId = ?")){

            st.setInt(1, deletedItemsId);
            try(ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Association a = getAssociationFromRS(rs);

                    int type;

                    if (parentId == -1) {
                        parentId = a.getId();
                    }

                    if (a.getAssociationtype() != AssociationType.SHORTCUT) {

                        type = AssociationType.DEFAULT_POSTING_FOR_SITE;

                        countSt.setInt(1, a.getContentId());
                        countSt.setInt(2, a.getSiteId());
                        try(ResultSet cntRs = countSt.executeQuery()) {
                            if (cntRs.next()) {
                                int cnt = cntRs.getInt("Cnt");
                                if (cnt > 0) {
                                    // Page is already published another place e.g cross posted
                                    type = AssociationType.CROSS_POSTING;
                                }
                            }
                        }
                    } else {
                        type = AssociationType.SHORTCUT;
                    }

                    updateSt.setInt(1, type);
                    updateSt.setInt(2, a.getId());
                    updateSt.executeUpdate();
                }
            }
            DeletedItemsAO.purgeDeletedItem(deletedItemsId);
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }
        return parentId;
    }

    public static List<String> findDuplicateAliases(Association parent) throws SystemException {
        List<String> duplicates = new ArrayList<>();

        try (Connection c = dbConnectionFactory.getConnection()){

            String sql = "SELECT Alias FROM content, associations WHERE ";
            if (dbConnectionFactory.isOracle()) {
                sql += " content.Alias IS NOT NULL ";
            } else {
                sql += " content.Alias <> '' ";
            }
            sql += " AND content.ContentId = associations.ContentId ";
            sql += " AND (associations.Path LIKE '%/" + parent.getId() + "/%' OR associations.ParentAssociationId = " + parent.getId() + ")";
            sql += " AND associations.IsDeleted = 0";

            try(PreparedStatement listSt = c.prepareStatement(sql);

            PreparedStatement aliasSt = c.prepareStatement("SELECT COUNT(DISTINCT(content.ContentId)) FROM content, associations WHERE content.Alias = ? and content.ContentId = associations.ContentId and associations.SiteId = ? and associations.IsDeleted = 0")) {

                try(ResultSet listRS = listSt.executeQuery()) {
                    while (listRS.next()) {
                        String alias = listRS.getString("Alias");

                        aliasSt.setString(1, alias);
                        aliasSt.setInt(2, parent.getSiteId());

                        try(ResultSet aliasRS = aliasSt.executeQuery()) {
                            if (aliasRS.next()) {
                                int antall = aliasRS.getInt(1);
                                if (antall > 1) {
                                    // Duplikat alias
                                    duplicates.add(alias);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", e);
        }

        return duplicates;
    }
}
