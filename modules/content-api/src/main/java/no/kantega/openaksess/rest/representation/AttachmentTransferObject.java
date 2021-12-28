package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.common.data.Attachment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.time.ZoneId;

@XmlAccessorType(XmlAccessType.NONE)
public class AttachmentTransferObject {
    private Attachment attachment;

    public AttachmentTransferObject(Attachment attachment) {
        this.attachment = attachment;
    }

    @XmlElement
    public int getId(){
        return attachment.getId();
    }

    @XmlElement
    public String getUrl(){
        return attachment.getUrl();
    }

    @XmlElement
    public String getLastModified(){
        if (attachment.getLastModified() == null) return null;
        return LocalDateTime.ofInstant(attachment.getLastModified().toInstant(), ZoneId.systemDefault()).toString();
    }

    @XmlElement
    public String getFilename(){
        return attachment.getFilename();
    }

    @XmlElement
    public int getContentId(){
        return attachment.getContentId();
    }

    @XmlElement
    public int getSize(){
        return attachment.getSize();
    }

    @XmlElement
    public String getMimeType(){
        return attachment.getMimeType().getType();
    }
}
