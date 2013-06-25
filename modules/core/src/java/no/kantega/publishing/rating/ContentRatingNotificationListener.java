package no.kantega.publishing.rating;

import no.kantega.publishing.api.rating.Rating;
import no.kantega.publishing.api.rating.RatingNotification;
import no.kantega.publishing.api.rating.RatingNotificationListener;
import no.kantega.publishing.common.ao.ContentAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ContentRatingNotificationListener implements RatingNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(ContentRatingNotificationListener.class);
    public void newRatingNotification(RatingNotification notification) {
        Rating r = notification.getRating();
        if (r.getContext().equalsIgnoreCase("content")) {
            try {
                ContentAO.setRating(Integer.parseInt(r.getObjectId()), notification.getScore(), notification.getNumberOfRatings());
            } catch (NumberFormatException nfe) {
                log.error( "Error parsing objectId: " + r.getObjectId());
            }            
        }
    }
}
