package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractSimpleEditContentAction implements Controller {

    private String view;

    protected abstract SecuritySession getSecuritySession(HttpServletRequest request);

    protected abstract Content getContentForEdit(HttpServletRequest request) throws InvalidFileException, ObjectLockedException, NotAuthorizedException, InvalidTemplateException;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getMethod().equalsIgnoreCase("POST")) {
            return saveContent(request, response);
        } else {
            ContentManagementService cms = new ContentManagementService(getSecuritySession(request));

            Content content = getContentForEdit(request);
            if (content.getId() != -1) {
                content = cms.checkOutContent(content.getContentIdentifier());
            }

            return showEditForm(request, response, content);
        }
    }

    protected ModelAndView showEditForm(HttpServletRequest request, HttpServletResponse response, Content content) throws ConfigurationException, InvalidFileException, ObjectLockedException, InvalidTemplateException, NotAuthorizedException {
        HttpSession session = request.getSession(true);

        RequestHelper.setRequestAttributes(request, content);

        String redirectUrl = request.getParameter("redirectUrl");
        if(redirectUrl != null && redirectUrl.length() > 0) {
            request.setAttribute("redirectUrl", redirectUrl);
        }
        request.setAttribute("currentContent", content);
        session.setAttribute("currentContent", content);

        Configuration config = Aksess.getConfiguration();
        Boolean allowArchive = Boolean.valueOf(config.getString("miniaksess.mediaarchive", "false"));
        Boolean allowWysiwyg = Boolean.valueOf(config.getString("miniaksess.wysiwyg", "true"));
        request.setAttribute("miniAksessMediaArchive", allowArchive);
        request.setAttribute("miniAksessWysiwyg", allowWysiwyg);

        addCustomRequestAttributes(request, content);

        return new ModelAndView(getView(), null);
    }

    protected void addCustomRequestAttributes(HttpServletRequest request, Content content) {

    }
    
    protected ModelAndView saveContent(HttpServletRequest request, HttpServletResponse response) throws InvalidFileException, InvalidTemplateException, RegExpSyntaxException, NotAuthorizedException, ObjectLockedException, ConfigurationException {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);

        Content content = (Content)session.getAttribute("currentContent");

        if (content != null) {
            // Page in session, save
            SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

            ValidationErrors errors = new ValidationErrors();
            errors = helper.getHttpParameters(errors);

            boolean isNew = content.getId() == -1;

            if (errors.getLength() == 0) {
                // No errors, save
                session.removeAttribute("errors");
                if (errors.getLength() == 0) {
                    ContentManagementService cms = new ContentManagementService(getSecuritySession(request));
                    content = cms.checkInContent(content, ContentStatus.PUBLISHED);
                }
                session.removeAttribute("currentContent");
                return postSaveContent(request, response, content, isNew);
            } else {
                return showEditForm(request, response, content);
            }
        }

        session.removeAttribute("adminMode");

        return new ModelAndView(new RedirectView(Aksess.getContextPath()));
    }

    protected ModelAndView postSaveContent(HttpServletRequest request, HttpServletResponse response, Content content, boolean isNew) {
        String url;
        String redirectUrl = request.getParameter("redirectUrl");
        if(redirectUrl != null && redirectUrl.length() > 0) {
            url = redirectUrl;
        } else {
            url = content.getUrl();
        }

        return new ModelAndView(new RedirectView(url));
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
