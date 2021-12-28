package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.multimedia.MultimediaDao;
import no.kantega.publishing.api.multimedia.MultimediaUsageDao;
import no.kantega.publishing.common.ao.rowmapper.ExifMetadataToMultimediaRowMapper;
import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.InputStreamHandler;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @see MultimediaDao
 */
public class JdbcMultimediaDao extends NamedParameterJdbcDaoSupport implements MultimediaDao {
    private static final String DB_TABLE = "multimedia";
    private static final String DB_COLS = "Id, ParentId, " + DB_TABLE + ".SecurityId, " + DB_TABLE + ".Type, Name, Author, Description, Filename, MediaSize, Width, Height, LastModified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, ProfileImageUserId, NoFiles, NoSubFolders, HasImageMap, NoUsages, " + DB_TABLE + ".ContentId, " + DB_TABLE + ".OwnerPerson";

    private final MultimediaRowMapper rowMapper = new MultimediaRowMapper();

    private MultimediaUsageDao multimediaUsageDao;

    public void deleteMultimedia(int id) throws ObjectInUseException {
        // Check if there are any children
        int noChildren = getJdbcTemplate().queryForObject("select COUNT(id) from multimedia where ParentId = ?", Integer.class, id);
        if (noChildren > 0) {
            throw new ObjectInUseException("Multimedia with id " + id + " is in use");
        }

        // Get parent id
        int parentId = getJdbcTemplate().queryForObject("select parentId from multimedia where Id = ?", Integer.class, id);

        getJdbcTemplate().update("delete from multimedia where Id = ?", id);

        if (parentId > 0) {
            updateNoSubFoldersAndFiles(parentId);
        }


        getJdbcTemplate().update("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?", id, ObjectType.MULTIMEDIA);

        deleteExistingExifData(id);

        // Delete usagecount
        multimediaUsageDao.removeMultimediaId(id);
    }


    public Multimedia getMultimedia(int id) {
        List<Multimedia> media = getJdbcTemplate().query("select " + DB_COLS + " from multimedia where Id = ?", rowMapper, id);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }

        return null;
    }


    public Multimedia getMultimediaByParentIdAndName(int parentId, String name) {
        List<Multimedia> media = getJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ParentId = ? AND Name = ?", rowMapper, parentId, name);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }
        return null;
    }


    public Multimedia getProfileImageForUser(String userId) {
        if (userId == null || userId.trim().equals("")) {
            return null;
        }

        List<Multimedia> media = getJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ProfileImageUserId = ?", rowMapper, userId);
        if (media.size() > 0) {
            return updateMultimediaWithExifData(media).get(0);
        }

        return null;
    }

    private List<Multimedia> updateMultimediaWithExifData(List<Multimedia> multimedia) {
        if (multimedia.size() == 0) {
            return multimedia;
        }

        String query = "SELECT * FROM multimediaexifdata WHERE MultimediaId IN (:ids) ORDER BY MultimediaId, Directory, ValueKey";
        List<Integer> ids = multimedia.stream().map(Multimedia::getId).collect(Collectors.toList());
        getNamedParameterJdbcTemplate().query(query, Collections.singletonMap("ids", ids), new ExifMetadataToMultimediaRowMapper(multimedia));

        return multimedia;
    }



    public void streamMultimediaData(final int id, final InputStreamHandler ish) {
        getJdbcTemplate().execute((ConnectionCallback) connection -> {
            try(PreparedStatement p = connection.prepareStatement("select Data from multimedia where Id = ?")) {
                p.setInt(1, id);
                try(ResultSet rs = p.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    Blob blob = rs.getBlob("Data");
                    try {
                        ish.handleInputStream(blob.getBinaryStream());
                    } catch (IOException e) {
                        // User has aborted download?
                        logger.error("Error streaming multimedia", e);
                    }
                }
            }
            return null;
        });
    }

    public List<Multimedia> getMultimediaList(int parentId) {
        return updateMultimediaWithExifData(getJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ParentId = ? AND ProfileImageUserId IS NULL AND ContentId < 0 ORDER BY Type, Name", rowMapper, parentId));
    }

    public List<Multimedia> getMultimediaWithContentId(int contentId) {
        return updateMultimediaWithExifData(getJdbcTemplate().query("SELECT " + DB_COLS + " FROM multimedia WHERE ContentId = ? AND ProfileImageUserId IS NULL ORDER BY Type, Name", rowMapper, contentId));
    }


    public int getMultimediaCount() {
        return getJdbcTemplate().queryForObject("SELECT COUNT(id) AS count FROM multimedia WHERE type = ?",  Integer.class, MultimediaType.MEDIA.getTypeAsInt());
    }

    public void moveMultimedia(int multimediaId, int newParentId) {
        // Get old parent id
        int oldParentId = getJdbcTemplate().queryForObject("SELECT parentId FROM multimedia WHERE Id = ? ", Integer.class, multimediaId);

        // Set new parent id
        getJdbcTemplate().update("UPDATE multimedia SET ParentId = ? WHERE Id = ?", newParentId, multimediaId);

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


        boolean hasData = multimedia.getData() != null;

        if (multimedia.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(getPreparedStatementCreator(multimedia), keyHolder);
            multimedia.setId(keyHolder.getKey().intValue());
        } else {
            getJdbcTemplate().update(getPreparedStatementCreator(multimedia));
        }

        if (hasData) {
            deleteExistingExifData(multimedia.getId());
            saveExifData(multimedia);
        }

        // Update parent count
        if (multimedia.getParentId() > 0) {
            updateNoSubFoldersAndFiles( multimedia.getParentId());
        }

        // Update usage count
        updateNumberOfUsages(multimedia.getId());

        return multimedia.getId();
    }

    private PreparedStatementCreator getPreparedStatementCreator(final Multimedia multimedia) {
        final boolean hasData = multimedia.getData() != null;

        return c -> {
            PreparedStatement st;
            byte[] data = multimedia.getData();
            if (multimedia.isNew()) {
                if (!hasData) {
                    st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, HasImageMap, ContentId, OwnerPerson, ProfileImageUserId) values(?,?,?,?,?,?,?,?,NULL,0,NULL,?,?,?,?,?,?,?,?,?,?,?,0,?,?,?)", new String[] {"ID"});
                } else {
                    st = c.prepareStatement("insert into multimedia (ParentId, SecurityId, Type, Name, Author, Description, Width, Height, Filename, MediaSize, Data, Lastmodified, LastModifiedBy, AltName, UsageInfo, OriginalDate, CameraMake, CameraModel, GPSLatitudeRef, GPSLatitude, GPSLongitudeRef, GPSLongitude, HasImageMap, ContentId, OwnerPerson, ProfileImageUserId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,?,?,?)", new String[] {"ID"});
                }
            } else {
                if (!hasData) {
                    st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ?, OriginalDate = ?, CameraMake = ?, CameraModel = ?, GPSLatitudeRef = ?, GPSLatitude = ?, GPSLongitudeRef = ?, GPSLongitude = ?, ContentId = ?, OwnerPerson = ? where Id = ?");
                } else {
                    st = c.prepareStatement("update multimedia set Name = ?, Author = ?, Description = ?, Width = ?, Height = ?, Filename = ?, MediaSize = ?, Data = ?, LastModified = ?, LastModifiedBy = ?, AltName = ?, UsageInfo = ?, OriginalDate = ?, CameraMake = ?, CameraModel = ?, GPSLatitudeRef = ?, GPSLatitude = ?, GPSLongitudeRef = ?, GPSLongitude = ?, ContentId = ?, OwnerPerson = ? where Id = ?");
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
            st.setInt(p++, multimedia.getContentId());
            st.setString(p++, multimedia.getOwnerPerson());

            if (!multimedia.isNew()) {
                st.setInt(p, multimedia.getId());
            } else {
                st.setString(p, multimedia.getProfileImageUserId());
            }

            return st;
        };
    }

    private void deleteExistingExifData(int multimediaId) {
        getJdbcTemplate().update("DELETE FROM multimediaexifdata WHERE MultimediaId = ?", multimediaId);
    }

    private void saveExifData(Multimedia multimedia) {
        for (ExifMetadata metadata : multimedia.getExifMetadata()) {
            String values[] = metadata.getValues();
            if (values != null) {
                for (String value : values) {
                    getJdbcTemplate().update("INSERT INTO multimediaexifdata (MultimediaId, Directory, ValueKey, Value) VALUES (?,?,?,?)", multimedia.getId(), metadata.getDirectory(), metadata.getKey(), value);
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
        int noFiles = getJdbcTemplate().queryForObject("select count(Id) as cnt from multimedia where ParentId = ? and Type = ?", Integer.class, parentId, MultimediaType.MEDIA.getTypeAsInt());
        int noSubFolders = getJdbcTemplate().queryForObject("select count(Id) as cnt from multimedia where ParentId = ? and Type = ?", Integer.class, parentId, MultimediaType.FOLDER.getTypeAsInt());
        getJdbcTemplate().update("update multimedia set NoFiles = ?, NoSubFolders = ? where Id = ?", noFiles, noSubFolders, parentId);
    }

    private void updateNumberOfUsages(int multimediaId) {
        int noUsages = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM multimediausage WHERE MultimediaId = ?", Integer.class, multimediaId);
        getJdbcTemplate().update("UPDATE multimedia SET NoUsages = ? WHERE Id = ?", noUsages, multimediaId);
    }

    @Required
    public void setMultimediaUsageDao(MultimediaUsageDao multimediaUsageDao) {
        this.multimediaUsageDao = multimediaUsageDao;
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
            mm.setNoUsages(rs.getInt("NoUsages"));
            mm.setOwnerPerson(rs.getString("OwnerPerson"));
            mm.setHasImageMap(rs.getInt("HasImageMap") > 0);

            return mm;
        }
    }
}
