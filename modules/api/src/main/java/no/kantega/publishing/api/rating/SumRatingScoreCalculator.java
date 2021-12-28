package no.kantega.publishing.api.rating;

import java.util.List;

/**
 *
 */
public class SumRatingScoreCalculator  implements ScoreCalculator {
    public float getScoreForRatings(List<Rating> ratings) {
        int sum = 0;

        if (ratings == null || ratings.size() == 0) {
            return 0;
        }

        for (Rating r : ratings) {
            sum += r.getRating();
        }

        return sum;
    }
}

