package no.kantega.publishing.api.attachment.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.util.InputStreamHandler;

import java.sql.Connection;
import java.util.List;

public interface AttachmentAO {
    int setAttachment(Connection c, Attachment attachment) throws SystemException;

    int setAttachment(Attachment attachment) throws SystemException;

    void deleteAttachment(int id) throws SystemException;

    Attachment getAttachment(int id) throws SystemException;

    void streamAttachmentData(int id, InputStreamHandler ish) throws SystemException;

    List<Attachment> getAttachmentList(ContentIdentifier cid) throws SystemException;

    void copyAttachment(int contentId, int newContentId);
}
