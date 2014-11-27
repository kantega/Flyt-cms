package no.kantega.publishing.rating.dao;

import no.kantega.publishing.api.rating.Rating;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:testContext.xml")
public class JdbcRatingDaoTest {
    private static final String CONTENT = "content";

    @Autowired
    private RatingDao dao;

    @Test
    public void testGetRatingsForObject() {

        List<Rating> ratings = dao.getRatingsForObject("1", CONTENT);

        assertEquals(ratings.size(), 1);
    }

    @Test
    public void testGetRatingForObject () {
        Rating r = new Rating();
        r.setUserid("andska");
        r.setContext(CONTENT);
        r.setObjectId("5");
        r.setRating(2);
        r.setDate(new Date());
        r.setComment("Comment");

        dao.saveOrUpdateRating(r);

        // Insert one rating
        List<Rating> ratings = dao.getRatingsForObject("5", CONTENT);
        assertEquals(1, ratings.size());
        Rating actual = ratings.get(0);
        assertEquals(r.getUserid(), actual.getUserid());
        assertEquals(r.getContext(), actual.getContext());
        assertEquals(r.getObjectId(), actual.getObjectId());
        assertEquals(r.getRating(), actual.getRating());
        assertEquals(r.getComment(), actual.getComment());
    }

    @Test
    public void testDeleteRatingsForObject() {

        Rating r = new Rating();
        r.setUserid("andska");
        r.setContext(CONTENT);
        r.setObjectId("5");
        r.setRating(2);
        r.setDate(new Date());
        r.setComment("Comment");

        dao.saveOrUpdateRating(r);

        // Insert one rating
        List<Rating> ratings = dao.getRatingsForObject("5", CONTENT);
        dao.saveOrUpdateRating(r);
        assertSame(ratings.size(), 1);

        // Delete rating
        dao.deleteRatingsForObject("5", CONTENT);
        ratings = dao.getRatingsForObject("5", CONTENT);

        assertEquals(ratings.size(), 0);

    }

    @Test
    public void testGetRatingsForUser() {
        // If we base the test on dao.getRatingsForUser("andska"), the test will fail if testDeleteSpecificRatingForUser is run before this test
        List<Rating> ratings = dao.getRatingsForUser("krisel");

        assertEquals(ratings.size(), 2);
    }

    @Test
    public void testGetSpecificRatingForUser() {
        List<Rating> ratings = dao.getRatingsForUser("andska", "2", "content");

        assertEquals(1, ratings.size());
    }

    @Test
    public void testDeleteSpecificRatingForUser() {
        List<Rating> ratings = dao.getRatingsForUser("andska", "4", "content");
        assertEquals(1, ratings.size());

        dao.deleteRatingsForUser("andska", "4", "content");

        ratings = dao.getRatingsForUser("andska", "4", "content");
        assertEquals(0, ratings.size());

    }
}
