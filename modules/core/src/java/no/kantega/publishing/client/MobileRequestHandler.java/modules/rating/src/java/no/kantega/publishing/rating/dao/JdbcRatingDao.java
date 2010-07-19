package no.kantega.publishing.rating.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;

import no.kantega.publishing.api.rating.Rating;

import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

/**
 *
 */
public class JdbcRatingDao extends JdbcDaoSupport implements RatingDao {
    private RatingRowMapper ratingRowMapper = new RatingRowMapper();

    @SuppressWarnings("unchecked")
    public List<Rating> getRatingsForObject(String objectId, String context) {
        return getJdbcTemplate().query("select * from ratings where objectId = ? and context = ? order by ratingDate desc", new Object[] {objectId, context}, ratingRowMapper);
    }


    public void deleteRatingsForObject(String objectId, String context) {
        getJdbcTemplate().update("delete from ratings where objectId = ? and context = ?", new Object[] {objectId, context});
    }


    public void saveOrUpdateRating(final Rating r) {
        getJdbcTemplate().update("delete from ratings where objectId = ? and context = ? and userId = ?", new Object[] {r.getObjectId(), r.getContext(), r.getUserid()});

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
                PreparedStatement st = c.prepareStatement("insert into ratings values(?,?,?,?,?)");
                st.setString(1, r.getUserid());
                st.setString(2, r.getObjectId());
                st.setString(3, r.getContext());
                st.setInt(4, r.getRating());
                st.setTimestamp(5, new java.sql.Timestamp(r.getDate().getTime()));
                return st;
            }
        });

    }

    @SuppressWarnings("unchecked")
    public List<Rating> getRatingsForUser(String userId) {        
        return getJdbcTemplate().query("select * from ratings where userId = ? order by ratingDate desc", new Object[] {userId}, ratingRowMapper);
    }

    private class RatingRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int i) throws SQLException {
            Rating r = new Rating();

            r.setUserid(rs.getString("userId"));
            r.setObjectId(rs.getString("objectId"));
            r.setContext(rs.getString("context"));
            r.setRating(rs.getInt("rating"));
            r.setDate(rs.getDate("ratingdate"));
            return r;
        }
    }

}
