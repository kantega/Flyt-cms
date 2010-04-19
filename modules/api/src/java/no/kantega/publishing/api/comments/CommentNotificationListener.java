package no.kantega.publishing.api.comments;


/**
 *
 */
public interface CommentNotificationListener {
    /**
     * Called whenever a comment is added 
     * @param notification
     */
    public void newCommentNotification(CommentNotification notification);

}
