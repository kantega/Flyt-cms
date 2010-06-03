package no.kantega.publishing.admin.content.action;

import java.io.IOException;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCreateParameters;
import no.kantega.publishing.common.data.ContentIdentifier;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractSimpleEditContentAction implements Controller {

    private String view;

    protected abstract boolean allowedToEdit(HttpServletRequest request, Content content);
    
    protected SecuritySession getSecuritySession(HttpServletRequest request) {
        return SecuritySession.getInstance(request);        
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isRequestToSaveContent(request)) {
            return saveContentModelAndView(request, response);
        } else {
            return loadContentModelAndView(request, response);
        }
    }

    private boolean isRequestToSaveContent(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST");
    }

    private ModelAndView loadContentModelAndView(HttpServletRequest request, HttpServletResponse response) throws InvalidFileException, ObjectLockedException, NotAuthorizedException, InvalidTemplateException, ServletException, ConfigurationException, IOException {
        Content content = getContentForEdit(request);
        if (isNewContent(content) || allowedToEdit(request, content)) {
            return editModelAndView(request, response, content);
        } else {
            return notAllowedToEditModelAndView(request, response);
        }
    }

    protected Content getContentForEdit(HttpServletRequest request) throws InvalidFileException, ObjectLockedException, NotAuthorizedException, InvalidTemplateException {
        if (isExistingContentId(requestedId(request))) {
            return existingPage(request);
        } else if (isExistingContentId(parentId(request))) {
            return newPage(request);
        } else {
            throw new InvalidParameterException("", "");
        }
    }

    private ModelAndView editModelAndView(HttpServletRequest request, HttpServletResponse response, Content content) throws NotAuthorizedException, InvalidFileException, InvalidTemplateException, ObjectLockedException, ConfigurationException {
        ContentManagementService cms = new ContentManagementService(getSecuritySession(request));
        if (contentExists(content)) {
            return showEditForm(request, response, cms.checkOutContent(content.getContentIdentifier()));
        }
        return showEditForm(request, response, content);
    }

    private ModelAndView notAllowedToEditModelAndView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecuritySession secSession = getSecuritySession(request);
        if (secSession.isLoggedIn()) {
            triggerForbiddenResponse(request, response);
        } else {
            secSession.initiateLogin(request, response);
        }
        return null;
    }   

    private void triggerForbiddenResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestHelper.setRequestAttributes(request, null);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/403.jsp").forward(request, response);
    }
    
    private boolean isNewContent(Content content) {
        return !contentExists(content);
    }

    private boolean contentExists(Content content) {
        return isExistingContentId(content.getId());
    }    
    
    private boolean isExistingContentId(int thisId) {
        return thisId != -1;
    }    
    
    private int requestedId(HttpServletRequest request) {
        return new RequestParameters(request).getInt("thisId");
    }


    private int parentId(HttpServletRequest request) {
        return new RequestParameters(request).getInt("parentId");
    }

    private Content newPage(HttpServletRequest request) throws InvalidFileException, InvalidTemplateException, NotAuthorizedException {
        ContentCreateParameters createParam = new ContentCreateParameters(request);
        return new ContentManagementService(request).createNewContent(createParam);
    }

    private Content existingPage(HttpServletRequest request) throws NotAuthorizedException {
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(requestedId(request));
        return new ContentManagementService(request).getContent(cid, false);
    }

    private ModelAndView showEditForm(HttpServletRequest request, HttpServletResponse response, Content content) throws ConfigurationException, InvalidFileException, ObjectLockedException, InvalidTemplateException, NotAuthorizedException {
        HttpSession session = request.getSession(true);

        RequestHelper.setRequestAttributes(request, content);

        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && redirectUrl.length() > 0) {
            request.setAttribute("redirectUrl", redirectUrl);
        }
        request.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);
        session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);

        Configuration config = Aksess.getConfiguration();
        Boolean allowArchive = Boolean.valueOf(config.getString("miniaksess.mediaarchive", "false"));
        request.setAttribute("miniAksessMediaArchive", allowArchive);

        return new ModelAndView(getView(), null);
    }

    private ModelAndView saveContentModelAndView(HttpServletRequest request, HttpServletResponse response) throws InvalidFileException, InvalidTemplateException, RegExpSyntaxException, NotAuthorizedException, ObjectLockedException, ConfigurationException {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);

        Content content = (Content) session.getAttribute("currentContent");

        if (content != null) {
            // Page in session, save
            SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

            ValidationErrors errors = new ValidationErrors();
            errors = helper.getHttpParameters(errors);

            if (errors.getLength() == 0) {
                // No errors, save
                session.removeAttribute("errors");
                if (errors.getLength() == 0) {
                    ContentManagementService cms = new ContentManagementService(getSecuritySession(request));
                    content = cms.checkInContent(content, ContentStatus.PUBLISHED);
                }
                session.removeAttribute("currentContent");

                String url;
                String redirectUrl = param.getString("redirectUrl");
                if (redirectUrl != null && redirectUrl.length() > 0) {
                    url = redirectUrl;
                } else {
                    url = content.getUrl();
                }

                session.removeAttribute("adminMode");

                return new ModelAndView(new RedirectView(url));
            } else {
                return showEditForm(request, response, content);
            }
        }

        session.removeAttribute("adminMode");

        return new ModelAndView(new RedirectView(Aksess.getContextPath()));
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

}
