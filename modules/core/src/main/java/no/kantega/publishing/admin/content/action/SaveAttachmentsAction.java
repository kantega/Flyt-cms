package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SaveAttachmentsAction extends AbstractSaveContentAction {
    private String view;

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException{
        return new ValidationErrors();

    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getModel(Content content, HttpServletRequest request) {
        Map<String, Object> model =  new HashMap<>();

        ContentManagementService cms = new ContentManagementService(request);

        List<Attachment> attachments;
        if (content.isNew()) {
            attachments = content.getAttachments();
        } else {
            ContentIdentifier cid = ContentIdentifier.fromContentId(content.getId());
            cid.setLanguage(content.getLanguage());
            attachments = cms.getAttachmentList(cid);
        }

        model.put("attachments", attachments);

        return model;
    }

    public void setView(String view) {
        this.view = view;
    }
}