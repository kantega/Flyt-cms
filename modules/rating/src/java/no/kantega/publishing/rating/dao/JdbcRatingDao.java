package no.kantega.publishing.rating.dao;

import no.kantega.publishing.api.rating.Rating;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JdbcRatingDao extends JdbcDaoSupport implements RatingDao {
    private RatingRowMapper ratingRowMapper = new RatingRowMapper();

    @SuppressWarnings("unchecked")
    @Override
    public List<Rating> getRatingsForObjects(List<String> objectIds, String context) {
        if (objectIds.size() == 0) {
            return new ArrayList<>();
        }

        StringBuilder objectIdList = new StringBuilder();

        for (int i = 0, objectIdsSize = objectIds.size(); i < objectIdsSize; i++) {
            if (i > 0){
                objectIdList.append(",");
            }
            objectIdList.append(objectIds.get(i));
        }
        return getJdbcTemplate().query("select * from ratings where Context = ? AND ObjectId IN(" + objectIdList.toString() + ") order by RatingDate desc",new Object[] {context}, ratingRowMapper);
    }

    @Override
    public List<Rating> getRatingsForObject(String objectId, String context) {
        return getJdbcTemplate().query("select * from ratings where ObjectId = ? and Context = ? order by RatingDate desc", new Object[] {objectId, context}, ratingRowMapper);
    }

    @Override
    public void deleteRatingsForObject(String objectId, String context) {
        getJdbcTemplate().update("delete from ratings where ObjectId = ? and Context = ?", new Object[] {objectId, context});
    }

    @Override
    public void saveOrUpdateRating(final Rating r) {
        getJdbcTemplate().update("delete from ratings where ObjectId = ? and Context = ? and UserId = ?", new Object[] {r.getObjectId(), r.getContext(), r.getUserid()});

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                PreparedStatement st = c.prepareStatement("insert into ratings values(?,?,?,?,?,?)");
                st.setString(1, r.getUserid());
                st.setString(2, r.getObjectId());
                st.setString(3, r.getContext());
                st.setInt(4, r.getRating());
                st.setTimestamp(5, new java.sql.Timestamp(r.getDate().getTime()));
                st.setString(6, r.getComment());
                return st;
            }
        });

    }

    @Override
    public List<Rating> getRatingsForUser(String userId) {        
        return getJdbcTemplate().query("select * from ratings where UserId = ? order by RatingDate desc", ratingRowMapper, userId);
    }

    @Override
    public List<Rating> getRatingsForUser(String userId, String objectId, String context) {
        return getJdbcTemplate().query("select * from ratings where ObjectId = ? and Context = ? and UserId = ? order by RatingDate desc", ratingRowMapper, objectId, context, userId);
    }

    @Override
    public void deleteRatingsForUser(String userId, String objectId, String context) {
        getJdbcTemplate().update("delete from ratings where ObjectId = ? and Context = ? and UserId = ?", objectId, context, userId);
    }

    private class RatingRowMapper implements RowMapper<Rating> {
        public Rating mapRow(ResultSet rs, int i) throws SQLException {
            Rating r = new Rating();

            r.setUserid(rs.getString("UserId"));
            r.setObjectId(rs.getString("ObjectId"));
            r.setContext(rs.getString("Context"));
            r.setRating(rs.getInt("Rating"));
            r.setDate(rs.getDate("Ratingdate"));
            r.setComment(rs.getString("Comment"));
            return r;
        }
    }

}
