package no.kantega.publishing.rating.score;

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.SumRatingScoreCalculator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SumRatingScoreCalculatorTest {

    private SumRatingScoreCalculator calculator = new SumRatingScoreCalculator();;

    @Test
    public void testGetScoreForRatings() {
        List<Rating> ratings = new ArrayList<Rating>();

        Rating r1 = new Rating();
        r1.setRating(4);
        ratings.add(r1);

        Rating r2 = new Rating();
        r2.setRating(4);
        ratings.add(r2);

        Rating r3 = new Rating();
        r3.setRating(4);
        ratings.add(r3);

        Rating r4 = new Rating();
        r4.setRating(4);
        ratings.add(r4);

        float score = calculator.getScoreForRatings(ratings);
        assertEquals(score, 16.0f, 0);
    }

    @Test
    public void shouldReturnZeroWhenNoRatings() {
        float result = calculator.getScoreForRatings(new ArrayList<Rating>());
        assertEquals(0, result, 0);
    }
}
