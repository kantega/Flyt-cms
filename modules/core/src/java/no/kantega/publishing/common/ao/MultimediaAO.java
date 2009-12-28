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
import no.kantega.commons.log.Log;
import no.kantega.publishing.client.MultimediaRequestHandler;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.security.ao.PermissionsAO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MultimediaAO {
    private static final String SOURCE = "aksess.MultimediaAO";

    private static final String DB_TABLE = "multimedia";
    private static final String DB_COLS = "Id, ParentId, " + DB_TABLE + ".SecurityId, " + DB_TABLE + ".Type, Name, Author, Description, Filename, MediaSize, Width, Height, LastModified, LastModifiedBy, AltName, UsageInfo, NoFiles, NoSubFolders";


    /**
     * Sletter et multimediaobjekt
     * @param id - Id til objekt som skal slettes
     * @throws SystemException
     * @throws ObjectInUseException - Hvis det finnes underobjekter
     */
    public static void deleteMultimedia(int id) throws SystemException, ObjectInUseException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            // Først sjekk om det finnes underlementer
            ResultSet rs = SQLHelper.getResultSet(c, "select * from multimedia where ParentId = " + id);
            if (rs.next()) {
                rs.close();
                rs = null;
                throw new ObjectInUseException(SOURCE, "");
            }

            // Get parent id
            int parentId = SQLHelper.getInt(c, "select parentId from multimedia where Id = " + id, "ParentId");

            PreparedStatement st = c.prepareStatement("delete from multimedia where Id = ?");
            st.setInt(1, id);
            st.execute();
            st.close();

            if (parentId > 0) {
                updateNoSubFoldersAndFiles(c, parentId);
            }

            // Slett eventuelle tilgangsrettigheter
            st = c.prepareStatement("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?");
            st.setInt(1, id);
            st.setInt(2, ObjectType.MULTIMEDIA);
            st.execute();
            st.close();

            // Delete usagecount
            MultimediaUsageAO.removeMultimediaId(id);


        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "SQL feil ved sletting av multimediaobjekt", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Update number of subfolders and files in a folder
     * @param c - connection
     * @param parentId - parent
     * @throws SQLException
     */
    private static void updateNoSubFoldersAndFiles(Connection c, int parentId) throws SQLException {
        int noFiles = SQLHelper.getInt(c, "select count(Id) as cnt from multimedia where ParentId = " + parentId + " and Type = " + MultimediaType.MEDIA.getTypeAsInt(), "cnt");
        int noSubFolders = SQLHelper.getInt(c, "select count(Id) as cnt from multimedia where ParentId = " + parentId + " and Type = " + MultimediaType.FOLDER.getTypeAsInt(), "cnt");
        PreparedStatement st = c.prepareStatement("update multimedia set NoFiles = ?, NoSubFolders = ? where Id = ?");
        st.setInt(1, noFiles);
        st.setInt(2, noSubFolders);
        st.setInt(3, parentId);
        st.execute();
        st.close();
    }


    /**
     * Henter multimedia objekt fra basen (unntatt data)
     * @param id - Id til objekt som skal hentes
     * @return
     * @throws SystemException
     */
    public static Multimedia getMultimedia(int id) throws SystemException {
        Connection c = null;

        String query = "select " + DB_COLS + " from multimedia where Id = " + id;

        try {
            c = dbConnectionFactory.getConnection();

            // Hent content og contentversion
            ResultSet rs = SQLHelper.getResultSet(c, query);
            if (!rs.next()) {
                return null;
            }
            Multimedia mm = getMultimediaFromRS(rs);
            rs.close();
            return mm;
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
    }


    /**
     * Sender multimedia til klienten
     * @param id - Id på objekt som skal streames
     * @param ish - Inputhandler som håndterer stream
     * @throws SystemException
     */
    public static void streamMultimediaData(int id, InputStreamHandler ish) throws SystemException {
        Blob blob   = null;

        Connection c = null;

        String query = "select Data from multimedia where Id = " + id;
        try {
            c = dbConnectionFactory.getConnection();
            // Hent content og contentversion
            ResultSet rs = SQLHelper.getResultSet(c, query);
            if (!rs.next()) {
                return;
            }
            blob = rs.getBlob("Data");
            ish.handleInputStream(blob.getBinaryStream());

            rs.close();
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } catch (IOException e) {
            // Brukeren har avbrutt
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }


    /**
     * Henter alle objekter i multimediaarkiv med angitt parentId
     * @param parentId - id til foreldremappe
     * @return
     * @throws SystemException
     */
    public static List<Multimedia> getMultimediaList(int parentId) throws SystemException {
        Connection c = null;

        List<Multimedia> mmList = new ArrayList<Multimedia>();

        String query = "select " + DB_COLS + " from multimedia where ParentId = " + parentId + " order by Type, Name";

        try {
            c = dbConnectionFactory.getConnection();
            // Hent content og contentversion
            ResultSet rs = SQLHelper.getResultSet(c, query);
            while(rs.next()) {
                Multimedia mm = getMultimediaFromRS(rs);
                mmList.add(mm);
            }
            rs.close();
            return mmList;
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
    }


    /**
     * Searches the multimedia-archive for the given criteria
     *
     * @param phrase the text to search for. If this is a number it is interpreted as an ID to search for. If not,
     *               this string is searched for in names, authors, and descriptions.
     * @param site the site to limit the search by, or -1 for global.
     * @param parentId the root of the subtree of contents to limit the search by, or -1 for all
     * @return a list of Multimedia-objects matching the given criteria
     */
    public static List<Multimedia> searchMultimedia(String phrase, int site, int parentId) throws SystemException {
        List<Multimedia> mmList = new ArrayList<Multimedia>();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            int id = -1;
            try {
                id = Integer.parseInt(phrase);
            } catch (NumberFormatException e) {

            }

            String where = "";
            if (id != -1) {
                where = "Id = ?";
            } else {
                where = "Name like ? or Author like ? or Description like ?";
                String driver = dbConnectionFactory.getDriverName();
                if ((driver.indexOf("oracle") != -1) || (driver.indexOf("postgresql") != -1)) {
                    phrase = phrase.toLowerCase();
                    where = "lower(Name) like ? or lower(Author) like ? or lower(Description) like ?";
                }
            }

            String join = "";
            String where2 = "";
            if (site != -1 || parentId != -1) {
                join = "LEFT JOIN multimediausage ON multimedia.Id=multimediausage.MultimediaId LEFT JOIN associations ON associations.ContentId=multimediausage.ContentId";
                if (site != -1) {
                    where2 = "SiteId = ?";
                    if (parentId != -1) {
                        where2 += " and Path LIKE ?";
                    }
                } else {
                    where2 = "path LIKE ?";
                }
            }

            StringBuilder query = new StringBuilder();
            query.append("select ");
            query.append(DB_COLS);
            if (site != -1 || parentId != -1) {
                query.append(", associations.Path, associations.SiteId");
            }
            query.append(" from multimedia ");

            // join
            query.append(join);
            query.append(" ");
            
            query.append("where " + DB_TABLE + ".Type = ? and (");
            query.append(where);
            query.append(") ");

            if (!"".equals(where2)) {
                query.append("AND (");
                query.append(where2);
                query.append(") ");
            }

            query.append("order by Name");

            PreparedStatement st = c.prepareStatement(query.toString());
            int paramIdx = 0;
            st.setInt(++paramIdx, MultimediaType.MEDIA.getTypeAsInt());
            if (id != -1) {
                st.setInt(++paramIdx, id);
            } else {
                st.setString(++paramIdx, phrase + "%");
                st.setString(++paramIdx, phrase + "%");
                st.setString(++paramIdx, "%" + phrase + "%");
            }

            if (site != -1 || parentId != -1) {
                if (site != -1) {
                    st.setInt(++paramIdx, site);
                    if (parentId != -1) {
                        st.setString(++paramIdx, "%/" + parentId + "/%");
                    }
                } else {
                    st.setString(++paramIdx, "%/" + parentId + "/%");
                }
            }

            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                Multimedia mm = getMultimediaFromRS(rs);
                mmList.add(mm);
            }
            rs.close();
            return mmList;
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
    }


    /**
     * Flytter et multimediaobjekt
     * @param mmId - Id til objekt som skal flyttes
     * @param newParentId - Ny plassering for objekt
     * @throws SystemException
     */
    public static void moveMultimedia(int mmId, int newParentId) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            // Get parent id
            int oldParentId = SQLHelper.getInt(c, "select parentId from multimedia where Id = " + mmId, "ParentId");

            // Set new parent id
            PreparedStatement st =  c.prepareStatement("update multimedia set ParentId = ? where Id = ?");
            st.setInt(1, newParentId);
            st.setInt(2, mmId);
            st.execute();

            // Update count in old and new folder
            if (oldParentId > 0) {
                updateNoSubFoldersAndFiles(c, oldParentId);
            }
            if (newParentId > 0) {
                updateNoSubFoldersAndFiles(c, newParentId);
            }
        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "SQL feil ved flytting av multimediaobjekt", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }

    }


    /**
     * Lagre multimedia objekt i basen
     * @param mm Multimediaobjekt
     * @return
     * @throws SystemException
     */
    public static int setMultimedia(Multimedia mm) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = null;
            byte[] data = mm.getData();
            if (mm.getId() == -1) {
                // Ny
                if (data == null) {
                    st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, NoFiles, NoSubFolders) values(?,?,?,?,?,?,?,?,NULL,0,NULL,?,?,?,?,0,0)", new String[] {"Id"});
                } else {
                    st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, NoFiles, NoSubFolders) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,0)", new String[] {"Id"});
                }
            } else {
                // Oppdater
                if (data == null) {
                    st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ? where Id = ?");
                } else {
                    st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, Filename = ?, MediaSize = ?, Data = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ? where Id = ?");
                }
            }

            int p = 1;
            if (mm.getId() == -1) {
                st.setInt(p++, mm.getParentId());
                st.setInt(p++, mm.getSecurityId());
                st.setInt(p++, mm.getType().getTypeAsInt());
            }
            st.setString(p++, mm.getName());
            st.setString(p++, mm.getAuthor());
            st.setString(p++, mm.getDescription());
            st.setInt(p++, mm.getWidth());
            st.setInt(p++, mm.getHeight());

            if (data != null) {
                st.setString(p++, mm.getFilename());
                st.setInt(p++, mm.getSize());
                st.setBinaryStream(p++, new ByteArrayInputStream(data), data.length);
                try {
                    MultimediaRequestHandler.thumbnailCache.flushGroup(Integer.toString(mm.getId()));
                } catch (NullPointerException e) {
                    // Får nullpeker dersom group ikke finnes
                }
            }
            st.setTimestamp(p++, new java.sql.Timestamp(new java.util.Date().getTime()));
            st.setString(p++, mm.getModifiedBy());
            st.setString(p++, mm.getAltname());
            st.setString(p++, mm.getUsage());

            if (mm.getId() != -1) st.setInt(p++, mm.getId());

            st.execute();

            if (data != null) {
                data = null;
            }

            if (mm.getId() == -1) {
                // Finn id til det nye objektet
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    mm.setId(rs.getInt(1));
                }
            }

            // Update parent count
            if (mm.getParentId() > 0) {
                updateNoSubFoldersAndFiles(c, mm.getParentId());
            }

            if (mm.getParentId() == 0 && mm.getSecurityId() == -1) {
                PermissionsAO.setPermissions(mm, null);
                mm.setSecurityId(mm.getId());
            }

        } catch (SQLException e) {
            Log.error(SOURCE, e, null, null);
            throw new SystemException(SOURCE, "SQL feil ved lagring av multimediaobjekt", e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
            }
        }

        return mm.getId();
    }


    private static Multimedia getMultimediaFromRS(ResultSet rs) throws SQLException {
        Multimedia mm = new Multimedia();

        mm.setId(rs.getInt("Id"));
        mm.setParentId(rs.getInt("ParentId"));
        mm.setSecurityId(rs.getInt("SecurityId"));
        mm.setType(MultimediaType.getMultimediaTypeAsEnum(rs.getInt("Type")));
        mm.setName(rs.getString("Name"));
        mm.setAuthor(rs.getString("Author"));
        mm.setDescription(rs.getString("Description"));
        mm.setFilename(rs.getString("Filename"));
        mm.setSize(rs.getInt("MediaSize"));
        mm.setWidth(rs.getInt("Width"));
        mm.setHeight(rs.getInt("Height"));
        mm.setLastModified(rs.getTimestamp("LastModified"));
        mm.setModifiedBy(rs.getString("LastModifiedBy"));
        mm.setAltname(rs.getString("AltName"));
        mm.setUsage(rs.getString("UsageInfo"));
        mm.setNoFiles(rs.getInt("NoFiles"));
        mm.setNoSubFolders(rs.getInt("NoSubFolders"));

        return mm;
    }


    private static void setSecurityIdForChildren(Connection c, int parentId, int oldSecurityId, int newSecurityId) throws SQLException {
        ResultSet rs = SQLHelper.getResultSet(c, "select Id, Type from multimedia where ParentId = " + parentId + " and SecurityId = " + oldSecurityId);
        PreparedStatement st = c.prepareStatement("update multimedia set SecurityId = ? where Id = ?");

        while(rs.next()) {
            int id = rs.getInt("Id");
            MultimediaType type = MultimediaType.getMultimediaTypeAsEnum(rs.getInt("Type"));
            st.setInt(1, newSecurityId);
            st.setInt(2, id);
            st.execute();
            if (type == MultimediaType.FOLDER) {
                setSecurityIdForChildren(c, id, oldSecurityId, newSecurityId);
            }
        }
    }


    /**
     * Setter securityId til angitt objekt, samt alle underobjekter lik angitt objekts id
     * @param c - Databasekopling
     * @param object - objekt som det skal settes ny securityid for
     * @throws SQLException -
     */
    public static void setSecurityId(Connection c, BaseObject object, boolean setFromParent) throws SQLException {
        int securityId = object.getId();
        if (setFromParent) {
            Multimedia mm = (Multimedia)object;
            securityId = mm.getParentId();
        }
        PreparedStatement st = c.prepareStatement("update multimedia set SecurityId = ? where Id = ?");
        st.setInt(1, securityId);
        st.setInt(2, object.getId());
        st.execute();
        setSecurityIdForChildren(c, object.getId(), object.getSecurityId(), securityId);
    }

}
