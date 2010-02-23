package no.kantega.publishing.rating.dao;

import org.junit.Test;
import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.test.database.HSQLDBDatabaseCreator;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

/**
 *
 */
public class JdbcRatingDaoTest extends TestCase {
    private static final String CONTENT = "content";

    public void testGetRatingsForObject() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-rating-db.sql")).createDatabase();

        JdbcRatingDao dao = new JdbcRatingDao();
        dao.setDataSource(dataSource);

        List<Rating> ratings = dao.getRatingsForObject("1", CONTENT);

        assertSame(ratings.size(), 1);
    }

    public void testDeleteRatingsForObject() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-rating-db.sql")).createDatabase();

        JdbcRatingDao dao = new JdbcRatingDao();
        dao.setDataSource(dataSource);

        Rating r = new Rating();
        r.setUserid("andska");
        r.setContext(CONTENT);
        r.setObjectId("5");
        r.setRating(2);
        r.setDate(new Date());

        dao.saveOrUpdateRating(r);

        // Insert one rating
        List<Rating> ratings = dao.getRatingsForObject("5", CONTENT);
        dao.saveOrUpdateRating(r);
        assertSame(ratings.size(), 1);

        // Delete rating
        dao.deleteRatingsForObject("5", CONTENT);
        ratings = dao.getRatingsForObject("5", CONTENT);

        assertSame(ratings.size(), 0);

    }

    public void testGetRatingsForUser() {
        DataSource dataSource = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-rating-db.sql")).createDatabase();

        JdbcRatingDao dao = new JdbcRatingDao();
        dao.setDataSource(dataSource);

        List<Rating> ratings = dao.getRatingsForUser("andska");

        assertSame(ratings.size(), 2);
    }
}
