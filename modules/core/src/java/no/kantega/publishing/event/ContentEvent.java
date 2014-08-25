package no.kantega.publishing.event;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.data.User;

import java.util.Map;

/**
 * Container object for objects which were updated.  Which objects have values depends on the event.  E.g for a contentExpired event
 * the content object is set, for a associationUpdated event the association object is set 
 */
public class ContentEvent {

    private Content content;
    private Association association;
    private Attachment attachment;
    private Map<String, Object> model;
    private boolean canDelete;
    private User user;

    public Content getContent() {
        return content;
    }

    public ContentEvent setContent(Content content) {
        this.content = content;
        return this;
    }

    public Association getAssociation() {
        return association;
    }

    public ContentEvent setAssociation(Association association) {
        this.association = association;
        return this;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public ContentEvent setAttachment(Attachment attachment) {
        this.attachment = attachment;
        return this;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public ContentEvent setModel(Map<String, Object> model) {
        this.model = model;
        return this;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public ContentEvent setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        return this;
    }

    public ContentEvent setUser(User user) {
        this.user = user;
        return this;
    }

    public User getUser() {
        return user;
    }
}
