package no.kantega.publishing.comments;

import no.kantega.publishing.api.comments.CommentNotification;
import no.kantega.publishing.api.comments.CommentNotificationListener;
import no.kantega.publishing.common.ao.ContentAOJdbcImpl;
import no.kantega.publishing.content.api.ContentAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class ContentCommentNotificationListener implements CommentNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(ContentCommentNotificationListener.class);
    @Autowired
    private ContentAO contentAO;

    public void newCommentNotification(CommentNotification notification) {
        updateNrOfComments(notification);
    }


    public void commentDeletedNotification(CommentNotification notification) {
        updateNrOfComments(notification);
    }


    private void updateNrOfComments(CommentNotification notification) {
        if (notification.getContext().equalsIgnoreCase("content")) {
            try {
                ((ContentAOJdbcImpl)contentAO).setNumberOfComments(Integer.parseInt(notification.getObjectId()), notification.getNumberOfComments());
            } catch (NumberFormatException nfe) {
                log.error( "Error parsing objectId: " + notification.getObjectId());
            }
        }
    }
}
