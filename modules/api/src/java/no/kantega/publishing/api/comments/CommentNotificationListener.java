package no.kantega.publishing.api.comments;


/**
 *
 */
public interface CommentNotificationListener {
    /**
     * Called whenever a comment is added 
     * @param notification The notification
     */
    void newCommentNotification(CommentNotification notification);
    /**
     * Called whenever a comment is deleted
     * @param notification The notification
     */
    void commentDeletedNotification(CommentNotification notification);

}
