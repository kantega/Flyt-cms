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
    public void testDeleteRatingsForObject() {

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

        assertEquals(ratings.size(), 0);

    }

    @Test
    public void testGetRatingsForUser() {
        List<Rating> ratings = dao.getRatingsForUser("andska");

        assertEquals(ratings.size(), 2);
    }
}
