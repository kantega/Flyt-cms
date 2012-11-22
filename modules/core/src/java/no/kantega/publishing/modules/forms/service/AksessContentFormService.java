package no.kantega.publishing.modules.forms.service;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.service.FormService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.security.SecuritySession;

public class AksessContentFormService implements FormService {
    public Form getFormById(int formId) {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());
        ContentIdentifier cid =  ContentIdentifier.fromContentId(formId);
        try {
            Content content = cms.getContent(cid);
            return new AksessContentForm(content);
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e);
        }

        return null;
    }
}
