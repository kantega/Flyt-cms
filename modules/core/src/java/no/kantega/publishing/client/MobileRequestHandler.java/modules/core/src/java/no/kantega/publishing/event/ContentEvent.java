package no.kantega.publishing.event;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;

import java.util.Map;

/**
 * User: Kristian Selnæs
 * Date: 12.mai.2010
 * Time: 15:09:28
 */
public class ContentEvent {

    private Content content;
    private Association association;
    private Attachment attachment;
    private Map<String, Object> model;
    private boolean canDelete;

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
}
