package no.kantega.publishing.modules.forms.service;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.service.FormService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AksessContentFormService implements FormService {
    private static final Logger log = LoggerFactory.getLogger(AksessContentFormService.class);
    public Form getFormById(int formId) {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());
        ContentIdentifier cid =  ContentIdentifier.fromContentId(formId);
        try {
            Content content = cms.getContent(cid);
            return new AksessContentForm(content);
        } catch (NotAuthorizedException e) {
            log.error("", e);
        }

        return null;
    }
}
