package no.kantega.publishing.modules.linkcheck.check;

import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentHandler;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.jobs.alerts.UnusedAttachmentsFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AttachmentCheckJob {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnusedAttachmentsFinder unusedAttachmentsFinder;

    @Autowired
    private ContentAO contentAO;

    public void executeAttachmentCheckJob() {
        log.info("Starting AttachmentCheckJob");

        contentAO.forAllContentObjects(new ContentHandler() {
            @Override
            public void handleContent(Content content) {
                List<Attachment> attachments = AttachmentAO.getAttachmentList(content.getContentIdentifier());
                if(!attachments.isEmpty()) {
                    checkAttachments(content, attachments);
                }
            }
        }, new ContentAO.ContentHandlerStopper() {
            @Override
            public boolean isStopRequested() {
                return false;
            }
        });
    }

    private void checkAttachments(Content content, List<Attachment> attachments) {
        log.info("Checking attachments({}) for content {}", attachments.size(), content.getId());
        try {
            List<Integer> unusedAttachments = unusedAttachmentsFinder.getUnusedAttachmentsForContent(content.getId());
            for (Attachment attachment : attachments) {
                if(unusedAttachments.contains(attachment.getId()) && attachment.isSearchable() && content.isSearchable()) {
                    log.info("Setting attachment {}({}) not searchable", attachment.getFilename(), attachment.getId());
                    attachment.setSearchable(false);
                    AttachmentAO.setAttachment(attachment);
                } else if (!attachment.isSearchable() && content.isSearchable()) {
                    log.info("Setting attachment {}({}) searchable", attachment.getFilename(), attachment.getId());
                    attachment.setSearchable(true);
                    AttachmentAO.setAttachment(attachment);
                }
            }
        } catch (ContentNotFoundException e) {
            log.error("Could not find content", e);
        }
    }
}
