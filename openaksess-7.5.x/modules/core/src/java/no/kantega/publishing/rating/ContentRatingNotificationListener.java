package no.kantega.publishing.rating;

import no.kantega.publishing.api.rating.RatingNotificationListener;
import no.kantega.publishing.api.rating.RatingNotification;
import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.commons.log.Log;

/**
 *
 */
public class ContentRatingNotificationListener implements RatingNotificationListener {
    public void newRatingNotification(RatingNotification notification) {
        Rating r = notification.getRating();
        if (r.getContext().equalsIgnoreCase("content")) {
            try {
                ContentAO.setRating(Integer.parseInt(r.getObjectId()), notification.getScore(), notification.getNumberOfRatings());
            } catch (NumberFormatException nfe) {
                Log.error(this.getClass().getName(), "Error parsing objectId: " + r.getObjectId(), null, null);
            }            
        }
    }
}
