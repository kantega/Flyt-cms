package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.jobs.alerts.UnusedAttachmentsFinder;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/attachment/")
public class EditAttachmentAction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnusedAttachmentsFinder unusedAttachmentsFinder;

    @Autowired
    private AttachmentAO attachmentAO;

    @RequestMapping(value = "/{attachmentId}/togglesearchable", method = RequestMethod.POST)
    public ResponseEntity setAttachmentSearchable(@PathVariable Integer attachmentId, HttpServletRequest request) {
        SecuritySession securitySession = SecuritySession.getInstance(request);
        ContentManagementService cms = new ContentManagementService(securitySession);
        Attachment attachment = attachmentAO.getAttachment(attachmentId);

        try {
            if(securitySession.isAuthorized(cms.getContent(ContentIdentifier.fromContentId(attachment.getContentId())), Privilege.UPDATE_CONTENT)) {
                attachment.setSearchable(!attachment.isSearchable());
                attachmentAO.setAttachment(attachment);
                log.info("{} set searchable {} on attachment {}", securitySession.getIdentity().getUserId(), attachment.isSearchable(), attachmentId);
                return new ResponseEntity(HttpStatus.OK);
            }
        } catch (NotAuthorizedException e) {
            log.warn("{} tried to update attachment {}, but was not authorized", securitySession.getIdentity().getUserId(), attachmentId);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/{attachmentId}/delete", method = RequestMethod.POST)
    public ResponseEntity deleteAttachment(@PathVariable Integer attachmentId, HttpServletRequest request) {
        SecuritySession securitySession = SecuritySession.getInstance(request);
        ContentManagementService cms = new ContentManagementService(securitySession);
        Attachment attachment = attachmentAO.getAttachment(attachmentId);

        try {
            if(securitySession.isAuthorized(cms.getContent(ContentIdentifier.fromContentId(attachment.getContentId())), Privilege.UPDATE_CONTENT)) {
                attachmentAO.deleteAttachment(attachmentId);
                log.info("{} deleted attachment {}", securitySession.getIdentity().getUserId(), attachment.isSearchable(), attachmentId);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        } catch (NotAuthorizedException e) {
            log.warn("{} tried to update attachment {}, but was not authorized", securitySession.getIdentity().getUserId(), attachmentId);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/content/{contentId}/unused", method = RequestMethod.GET)
    public ResponseEntity<List<Integer>> getUnusedAttachments(@PathVariable Integer contentId) {
        try {
            return new ResponseEntity<>(unusedAttachmentsFinder.getUnusedAttachmentsForContent(contentId), HttpStatus.OK);
        } catch (ContentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/content/{contentId}/usedByVersion", method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, List<Integer>>> getUsedAttachmentsByVersion(@PathVariable Integer contentId) {
         return new ResponseEntity<>(unusedAttachmentsFinder.attachmentIdsByContentVersion(contentId), HttpStatus.OK);
    }
}
