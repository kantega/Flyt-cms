package no.kantega.publishing.api.rating;


import java.util.List;

/**
 * ScoreCalculator calculating the average of the given Ratings.
 */
public class AverageRatingScoreCalculator implements ScoreCalculator {
    public float getScoreForRatings(List<Rating> ratings) {
        int sum = 0;

        for (Rating r : ratings) {
            sum += r.getRating();
        }

        return ((float)sum)/ratings.size();
    }
}
