package no.kantega.publishing.rating.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;

import no.kantega.publishing.api.rating.Rating;

import java.util.List;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 */
public class JdbcRatingDao extends JdbcDaoSupport implements RatingDao {
    private DataSource dataSource;
    private RatingRowMapper ratingRowMapper = new RatingRowMapper();

    public List<Rating> getRatingsForObject(String objectId, String context) {

        return null;
    }


    public void deleteRatingsForObject(String objectId, String context) {
        getJdbcTemplate().update("delete from ratings where objectId = ? and context = ?", new Object[] {objectId, context});
    }


    public void saveOrUpdateRating(Rating rating) {

    }


    public List<Rating> getRatingsForUser(String userId) {        
        return null;
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
