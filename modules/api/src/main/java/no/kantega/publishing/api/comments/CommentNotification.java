package no.kantega.publishing.api.comments;

/**
 *
 */
public class CommentNotification {
    private String objectId;
    private String context;
    private String commentId;
    private String commentTitle;
    private String commentAuthor;
    private String commentSummary;

    private int numberOfComments;

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentSummary() {
        return commentSummary;
    }

    public void setCommentSummary(String commentSummary) {
        this.commentSummary = commentSummary;
    }
}
