package no.kantega.publishing.api.rating;

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.ScoreCalculator;

import java.util.List;

/**
 *
 */
public class SumRatingScoreCalculator  implements ScoreCalculator {
    public float getScoreForRatings(List<Rating> ratings) {
        int sum = 0;

        for (Rating r : ratings) {
            sum += r.getRating();
        }

        return sum;
    }
}

