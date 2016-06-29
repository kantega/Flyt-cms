package no.kantega.publishing.admin.administration.action;

import com.google.common.base.Function;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.jobs.alerts.UnusedAttachmentsFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

import static com.google.common.collect.Lists.transform;

@Controller
@RequestMapping("/admin/administration/unusedAttachments")
public class UnusedAttachmentsAction {

    @Autowired
    private UnusedAttachmentsFinder unusedAttachmentsFinder;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String viewForm(Model model){
        model.addAttribute("unusedAttachments",
                transform(unusedAttachmentsFinder.getUnusedAttachments(), new Function<Attachment, UnusedAttachment>() {
                    @Override
                    public UnusedAttachment apply(Attachment input) {
                        int associationId = -1;
                        if(input.getContentId() > 0) {
                            ContentIdentifier cid = ContentIdentifier.fromContentId(input.getContentId());
                            contentIdHelper.assureAssociationIdSet(cid);
                            associationId = cid.getAssociationId();
                        }
                        return new UnusedAttachment(input, associationId);
                    }
                })
        );
        return "/WEB-INF/jsp/admin/administration/unusedAttachments.jsp";
    }


    public static class UnusedAttachment extends Attachment {
        private final Attachment attachment;
        private final int associationId;

        public UnusedAttachment(Attachment attachment, int associationId) {
            this.attachment = attachment;
            this.associationId = associationId;
        }

        @Override
        public int getId() {
            return attachment.getId();
        }

        @Override
        public String getFilename() {
            return attachment.getFilename();
        }

        @Override
        public Date getLastModified() {
            return attachment.getLastModified();
        }

        @Override
        public String getUrl() {
            return attachment.getUrl();
        }

        @Override
        public boolean isSearchable() {
            return attachment.isSearchable();
        }

        public int getAssociationId() {
            return associationId;
        }
    }
}
