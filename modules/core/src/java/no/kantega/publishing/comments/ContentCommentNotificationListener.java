package no.kantega.publishing.comments;

import no.kantega.publishing.api.comments.CommentNotification;
import no.kantega.publishing.api.comments.CommentNotificationListener;
import no.kantega.publishing.common.ao.ContentAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ContentCommentNotificationListener implements CommentNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(ContentCommentNotificationListener.class);
    public void newCommentNotification(CommentNotification notification) {
        updateNrOfComments(notification);
    }


    public void commentDeletedNotification(CommentNotification notification) {
        updateNrOfComments(notification);
    }


    private void updateNrOfComments(CommentNotification notification) {
        if (notification.getContext().equalsIgnoreCase("content")) {
            try {
                ContentAO.setNumberOfComments(Integer.parseInt(notification.getObjectId()), notification.getNumberOfComments());
            } catch (NumberFormatException nfe) {
                log.error( "Error parsing objectId: " + notification.getObjectId());
            }
        }
    }
}
