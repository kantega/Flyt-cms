package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ShowAttachmentsAction implements Controller {

    @Autowired
    private AttachmentAO attachmentAO;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);
        RequestParameters requestParameters = new RequestParameters(request);

        int contentId = requestParameters.getInt("contentId");
        Map<String, Object> model = new HashMap<>();
        ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
        model.put("currentContent", cms.getContent(cid));
        model.put("attachments", attachmentAO.getAttachmentList(cid));

        return new ModelAndView("/WEB-INF/jsp/admin/publish/popups/showAttachments.jsp", model);
    }
}
