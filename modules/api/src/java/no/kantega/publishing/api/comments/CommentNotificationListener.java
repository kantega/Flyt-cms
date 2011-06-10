package no.kantega.publishing.api.comments;


/**
 *
 */
public interface CommentNotificationListener {
    /**
     * Called whenever a comment is added 
     * @param notification
     */
    void newCommentNotification(CommentNotification notification);
    void commentDeletedNotification(CommentNotification notification);

}
