package no.kantega.publishing.rating.dao;

import no.kantega.publishing.api.rating.Rating;

import java.util.List;

/**
 *
 */
public interface RatingDao {
    List<Rating> getRatingsForObject(String objectId, String context);
    List<Rating> getRatingsForObjects(List<String> objectIds, String context);
    void deleteRatingsForObject(String objectId, String context);
    void saveOrUpdateRating(Rating rating);
    List<Rating> getRatingsForUser(String userId);
    List<Rating> getRatingsForUser(String userId, String objectId, String context);
    void deleteRatingsForUser(String userId, String objectId, String context);
    List<String> getAllUserIdsForContext(String context);
}
