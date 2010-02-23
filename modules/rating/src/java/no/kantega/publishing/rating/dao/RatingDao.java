package no.kantega.publishing.rating.dao;

import no.kantega.publishing.api.rating.Rating;

import java.util.List;

/**
 *
 */
public interface RatingDao {
    public List<Rating> getRatingsForObject(String objectId, String context);
    public void deleteRatingsForObject(String objectId, String context);
    public void saveOrUpdateRating(Rating rating);
    public List<Rating> getRatingsForUser(String userId);
}
