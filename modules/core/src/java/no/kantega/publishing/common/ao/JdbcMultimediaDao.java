package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.sqlsearch.dialect.SQLDialect;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.ao.rowmapper.ExifMetadataToMultimediaRowMapper;
import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.InputStreamHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @see MultimediaDao
 */
public class JdbcMultimediaDao extends SimpleJdbcDaoSupport implements MultimediaDao {
    private static final String DB_TABLE = "multimedia";
    private static final String DB_COLS = "Id, ParentId, " + DB_TABLE + ".SecurityId, " + DB_TABLE + ".Type, Name, Author, Description, Filename, MediaSize, Width, Height, LastModified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, ProfileImageUserId, NoFiles, NoSubFolders";

    private final MultimediaRowMapper rowMapper = new MultimediaRowMapper();

    private MultimediaUsageDao multimediaUsageDao;
    private SQLDialect sqlDialect;


    public void deleteMultimedia(int id) throws ObjectInUseException {
        // Check if there are any children
        int noChildren = getSimpleJdbcTemplate().queryForInt("select COUNT(id) from multimedia where ParentId = ?", id);
        if (noChildren > 0) {
            throw new ObjectInUseException(this.getClass().getSimpleName(), "");
        }

        // Get parent id
        int parentId = getSimpleJdbcTemplate().queryForInt("select parentId from multimedia where Id = ?", id);

        getSimpleJdbcTemplate().update("delete from multimedia where Id = ?", id);

        if (parentId > 0) {
            updateNoSubFoldersAndFiles(parentId);
        }


        getSimpleJdbcTemplate().update("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?", id, ObjectType.MULTIMEDIA);

        deleteExistingExifData(id);

        // Delete usagecount
        multimediaUsageDao.removeMultimediaId(id);
    }


    public Multimedia getMultimedia(int id) {
        List<Multimedia> media = getSimpleJdbcTemplate().query("select " + DB_COLS + " from multimedia where Id = ?", rowMapper, id);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }

        return null;
    }


    public Multimedia getMultimediaByParentIdAndName(int parentId, String name) {
        List<Multimedia> media = getSimpleJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ParentId = ? AND Name = ?", rowMapper, parentId, name);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }
        return null;
    }


    public Multimedia getProfileImageForUser(String userId) {
        if (userId == null || userId.trim().equals("")) {
            return null;
        }

        List<Multimedia> media = getSimpleJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ProfileImageUserId = ?", rowMapper, userId);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }

        return null;
    }

    private List<Multimedia> updateMultimediaWithExifData(List<Multimedia> multimedia) {
        if (multimedia.size() == 0) {
            return multimedia;
        }

        String query = getQueryForExifData(multimedia);
        getSimpleJdbcTemplate().query(query, new ExifMetadataToMultimediaRowMapper(multimedia));

        return multimedia;
    }

    private String getQueryForExifData(List<Multimedia> multimedia) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM multimediaexifdata WHERE MultimediaId IN (");

        for (int i = 0, multimediaSize = multimedia.size(); i < multimediaSize; i++) {
            Multimedia media = multimedia.get(i);
            if (i > 0) {
                query.append(",");
            }
            query.append(media.getId());
        }

        query.append(") ORDER BY MultimediaId, Directory, ValueKey");
        return query.toString();
    }

    public void streamMultimediaData(final int id, final InputStreamHandler ish) {
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                PreparedStatement p = connection.prepareStatement("select Data from multimedia where Id = " + id);
                ResultSet rs = p.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                Blob blob = rs.getBlob("Data");
                try {
                    ish.handleInputStream(blob.getBinaryStream());
                } catch (IOException e) {
                    // User has aborted download
                }
                return null;
            }
        });
    }

    public List<Multimedia> getMultimediaList(int parentId) {
        return updateMultimediaWithExifData(getSimpleJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ParentId = ? AND ProfileImageUserId IS NULL ORDER BY Type, Name", rowMapper, parentId));
    }

    public int getMultimediaCount() {
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(id) AS count FROM multimedia WHERE type = ?", MultimediaType.MEDIA.getTypeAsInt());
    }

    public List<Multimedia> searchMultimedia(String phrase, int site, int parentId) {
        List<Object> params = new ArrayList<Object>();

        params.add(MultimediaType.MEDIA.getTypeAsInt());

        String where;
        int id = -1;
        try {
            id = Integer.parseInt(phrase);
            where = "Id = ?";
            params.add(id);
        } catch (NumberFormatException e) {
            where = "Name like ? or Author like ? or Description like ? or Filename like ?";
            if (sqlDialect.searchIsCaseSensitive()) {
                phrase = phrase.toLowerCase();
                where = "lower(Name) like ? or lower(Author) like ? or lower(Description) like ? or lower(Filename) like ?";
            }
            params.add(phrase + "%");
            params.add(phrase + "%");
            params.add(phrase + "%");
            params.add(phrase + "%");
        }

        String join = "";
        String where2 = "";
        if (site != -1 || parentId != -1) {
            join = "LEFT JOIN multimediausage ON multimedia.Id=multimediausage.MultimediaId LEFT JOIN associations ON associations.ContentId=multimediausage.ContentId";
            if (site != -1) {
                where2 = "SiteId = ?";
                params.add(site);
                if (parentId != -1) {
                    where2 += " and Path LIKE ?";
                    params.add("%/" + parentId + "/%");
                }
            } else {
                where2 = "path LIKE ?";
                params.add("%/" + parentId + "/%");
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

        query.append("where ").append(DB_TABLE).append(".Type = ? and ProfileImageUserId is NULL and (");
        query.append(where);
        query.append(") ");

        if (!"".equals(where2)) {
            query.append("AND (");
            query.append(where2);
            query.append(") ");
        }

        query.append("order by Name");

        return updateMultimediaWithExifData(getSimpleJdbcTemplate().query(query.toString(), rowMapper, params.toArray()));
    }

    public void moveMultimedia(int multimediaId, int newParentId) {
        // Get old parent id
        int oldParentId = getSimpleJdbcTemplate().queryForInt("SELECT parentId FROM multimedia WHERE Id = ? ", multimediaId);

        // Set new parent id
        getSimpleJdbcTemplate().update("UPDATE multimedia SET ParentId = ? WHERE Id = ?", newParentId, multimediaId);

        // Update count in old and new folder
        if (oldParentId > 0) {
            updateNoSubFoldersAndFiles(oldParentId);
        }
        if (newParentId > 0) {
            updateNoSubFoldersAndFiles(newParentId);
        }
    }

    public int setMultimedia(final Multimedia multimedia) throws SystemException {
        // Name must be unique in folder for webdav support
        String name = getUniqueName(multimedia);
        multimedia.setName(name);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        final boolean hasData = multimedia.getData() != null;

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                PreparedStatement st;
                byte[] data = multimedia.getData();
                if (multimedia.isNew()) {
                    // Ny
                    if (!hasData) {
                        st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, ProfileImageUserId) values(?,?,?,?,?,?,?,?,NULL,0,NULL,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    } else {
                        st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, ProfileImageUserId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    }
                } else {
                    // Oppdater
                    if (!hasData) {
                        st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ?, OriginalDate = ?, CameraMake = ?, CameraModel = ?, GPSLatitudeRef = ? GPSLatitude = ?, GPSLongitudeRef = ?, GPSLongitude = ? where Id = ?");
                    } else {
                        st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, Filename = ?, MediaSize = ?, Data = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ?, OriginalDate = ?, CameraMake = ?, CameraModel = ?, GPSLatitudeRef = ?, GPSLatitude = ?, GPSLongitudeRef = ?, GPSLongitude = ? where Id = ?");
                    }
                }

                int p = 1;
                if (multimedia.isNew()) {
                    st.setInt(p++, multimedia.getParentId());
                    st.setInt(p++, multimedia.getSecurityId());
                    st.setInt(p++, multimedia.getType().getTypeAsInt());
                }
                st.setString(p++, multimedia.getName());
                st.setString(p++, multimedia.getAuthor());
                st.setString(p++, multimedia.getDescription());
                st.setInt(p++, multimedia.getWidth());
                st.setInt(p++, multimedia.getHeight());

                if (data != null) {
                    st.setString(p++, multimedia.getFilename());
                    st.setInt(p++, multimedia.getSize());
                    st.setBinaryStream(p++, new ByteArrayInputStream(data), data.length);
                }
                st.setTimestamp(p++, new java.sql.Timestamp(new java.util.Date().getTime()));
                st.setString(p++, multimedia.getModifiedBy());
                st.setString(p++, multimedia.getAltname());
                st.setString(p++, multimedia.getUsage());
                st.setTimestamp(p++, multimedia.getOriginalDate() != null ? new java.sql.Timestamp(multimedia.getOriginalDate().getTime()) : null);
                st.setString(p++, multimedia.getCameraMake());
                st.setString(p++, multimedia.getCameraModel());
                st.setString(p++, multimedia.getGpsLatitudeRef());
                st.setString(p++, multimedia.getGpsLatitude());
                st.setString(p++, multimedia.getGpsLongitudeRef());
                st.setString(p++, multimedia.getGpsLongitude());

                if (!multimedia.isNew()) {
                    st.setInt(p++, multimedia.getId());
                } else {
                    st.setString(p++, multimedia.getProfileImageUserId());
                }

                if (data != null) {
                    data = null;
                }

                return st;
            }
        },keyHolder);

        if (multimedia.isNew()) {
            multimedia.setId(keyHolder.getKey().intValue());
        }

        if (hasData) {
            deleteExistingExifData(multimedia.getId());
            saveExifData(multimedia);
        }

        // Update parent count
        if (multimedia.getParentId() > 0) {
            updateNoSubFoldersAndFiles( multimedia.getParentId());
        }

        return multimedia.getId();
    }

    private void deleteExistingExifData(int multimediaId) {
        getSimpleJdbcTemplate().update("DELETE FROM multimediaexifdata WHERE MultimediaId = ?", multimediaId);
    }

    private void saveExifData(Multimedia multimedia) {
        for (ExifMetadata metadata : multimedia.getExifMetadata()) {
            String values[] = metadata.getValues();
            if (values != null) {
                for (String value : values) {
                    getSimpleJdbcTemplate().update("INSERT INTO multimediaexifdata (MultimediaId, Directory, ValueKey, Value) VALUES (?,?,?,?)", multimedia.getId(), metadata.getDirectory(), metadata.getKey(), value);
                }
            }
        }
    }

    private String getUniqueName(Multimedia newMedia) {
        String name = newMedia.getName();
        int cnt = 2;

        Multimedia existing;
        do {
            existing = getMultimediaByParentIdAndName(newMedia.getParentId(), name);
            if (existing != null && existing.getId() != newMedia.getId()) {
                name = newMedia.getName() + cnt;
            } else {
                return name;
            }
            cnt++;

        } while (true);
    }


    private void updateNoSubFoldersAndFiles(int parentId) {
        int noFiles = getSimpleJdbcTemplate().queryForInt("select count(Id) as cnt from multimedia where ParentId = ? and Type = ?", parentId, MultimediaType.MEDIA.getTypeAsInt());
        int noSubFolders = getSimpleJdbcTemplate().queryForInt("select count(Id) as cnt from multimedia where ParentId = ? and Type = ?", parentId, MultimediaType.FOLDER.getTypeAsInt());
        getSimpleJdbcTemplate().update("update multimedia set NoFiles = ?, NoSubFolders = ? where Id = ?", noFiles, noSubFolders, parentId);
    }


    public void setMultimediaUsageDao(MultimediaUsageDao multimediaUsageDao) {
        this.multimediaUsageDao = multimediaUsageDao;
    }

    public void setSqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    private class MultimediaRowMapper implements RowMapper<Multimedia> {
        public Multimedia mapRow(ResultSet rs, int i) throws SQLException {
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
            mm.setOriginalDate(rs.getDate("OriginalDate"));
            mm.setCameraMake(rs.getString("CameraMake"));
            mm.setCameraModel(rs.getString("CameraModel"));
            mm.setGpsLatitudeRef(rs.getString("GPSLatitudeRef"));
            mm.setGpsLatitude(rs.getString("GPSLatitude"));
            mm.setGpsLongitudeRef(rs.getString("GPSLongitudeRef"));
            mm.setGpsLongitude(rs.getString("GPSLongitude"));
            mm.setProfileImageUserId(rs.getString("ProfileImageUserId"));
            mm.setNoFiles(rs.getInt("NoFiles"));
            mm.setNoSubFolders(rs.getInt("NoSubFolders"));

            return mm;
        }
    }
}