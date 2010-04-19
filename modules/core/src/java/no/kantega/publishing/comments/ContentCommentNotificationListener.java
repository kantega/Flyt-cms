package no.kantega.publishing.comments;

import no.kantega.publishing.api.comments.CommentNotificationListener;
import no.kantega.publishing.api.comments.CommentNotification;
import no.kantega.publishing.api.rating.RatingNotification;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.commons.log.Log;

/**
 *
 */
public class ContentCommentNotificationListener implements CommentNotificationListener {
    public void newCommentNotification(CommentNotification notification) {
        if (notification.getContext().equalsIgnoreCase("content")) {
            try {
                ContentAO.setNumberOfComments(Integer.parseInt(notification.getObjectId()), notification.getNumberOfComments());
            } catch (NumberFormatException nfe) {
                Log.error(this.getClass().getName(), "Error parsing objectId: " + notification.getObjectId(), null, null);
            }
        }
    }
}
