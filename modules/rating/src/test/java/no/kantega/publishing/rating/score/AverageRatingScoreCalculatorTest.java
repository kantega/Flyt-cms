package no.kantega.publishing.rating.score;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

import no.kantega.publishing.api.rating.Rating;

/**
 *
 */
public class AverageRatingScoreCalculatorTest extends TestCase {
    public void testGetScoreForRatings() {

        List<Rating> ratings = new ArrayList<Rating>();

        Rating r1 = new Rating();
        r1.setRating(3);
        ratings.add(r1);

        Rating r2 = new Rating();
        r2.setRating(4);
        ratings.add(r2);

        Rating r3 = new Rating();
        r3.setRating(3);
        ratings.add(r3);

        Rating r4 = new Rating();
        r4.setRating(4);
        ratings.add(r4);


        AverageRatingScoreCalculator calculator = new AverageRatingScoreCalculator();
        float score = calculator.getScoreForRatings(ratings);

        assertEquals(score, 3.5f);
    }
}
