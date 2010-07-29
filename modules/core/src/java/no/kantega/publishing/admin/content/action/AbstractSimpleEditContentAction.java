package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCreateParameters;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.ContentNotFoundException;
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

    /**
     * Implement this method to decide if user is allowed to edit this page.
     * @param request - HttpServletRequest
     * @param content - Content which is about to be edited
     * @return
     */
    protected abstract boolean isAllowedToEdit(HttpServletRequest request, Content content);

    /**
     * Get the securitysession to use when editing page.  Override to allow anonymous users to edit page etc.
     * @param request - HttpServletRequest
     * @return - SecuritySession
     */
    protected SecuritySession getSecuritySession(HttpServletRequest request) {
        return SecuritySession.getInstance(request);        
    }

    /**
     * Returns a content object to edit based on parameters from request
     * @param request - HttpServletRequest
     * @return
     * @throws InvalidFileException
     * @throws ObjectLockedException
     * @throws NotAuthorizedException
     * @throws InvalidTemplateException
     * @throws ContentNotFoundException
     */
    protected Content getContentForEdit(HttpServletRequest request) throws InvalidFileException, ObjectLockedException, NotAuthorizedException, InvalidTemplateException, ContentNotFoundException {
        RequestParameters param = new RequestParameters(request);
        if (param.getInt("thisId") != -1) {
            return getExistingPage(request);
        } else if (param.getInt("parentId") != -1) {
            return createNewPage(request);
        } else {
            throw new InvalidParameterException("", "");
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getMethod().equalsIgnoreCase("POST")) {
            // Save page
            return saveContent(request, response);
        } else {
            // Edit page
            Content content = getContentForEdit(request);
            if (isAllowedToEdit(request, content)) {
                ContentManagementService cms = new ContentManagementService(getSecuritySession(request));
                if (!content.isNew()) {
                    // Existing content must be checked out before edit
                    content = cms.checkOutContent(content.getContentIdentifier());
                }
                request.setAttribute(AdminRequestParameters.MINI_ADMIN_MODE, true);
                return showEditForm(request, content);
            } else {
                throw new NotAuthorizedException("Not authorized", this.getClass().getName());
            }
        }
    }

    private Content createNewPage(HttpServletRequest request) throws InvalidFileException, InvalidTemplateException, NotAuthorizedException {
        ContentCreateParameters createParam = new ContentCreateParameters(request);
        return new ContentManagementService(request).createNewContent(createParam);
    }

    private Content getExistingPage(HttpServletRequest request) throws NotAuthorizedException, SystemException, InvalidFileException, InvalidTemplateException, ObjectLockedException, ContentNotFoundException {
        ContentIdentifier cid = new ContentIdentifier(request);
        return new ContentManagementService(request).getContent(cid, false);
    }

    private ModelAndView showEditForm(HttpServletRequest request, Content content) throws ConfigurationException, InvalidFileException, ObjectLockedException, InvalidTemplateException, NotAuthorizedException {
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

    private ModelAndView saveContent(HttpServletRequest request, HttpServletResponse response) throws InvalidFileException, InvalidTemplateException, RegExpSyntaxException, NotAuthorizedException, ObjectLockedException, ConfigurationException {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);
        ContentManagementService cms = new ContentManagementService(getSecuritySession(request));

        Content content = (Content) session.getAttribute("currentContent");

        if (content != null) {
            // Page in session, save
            SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

            ValidationErrors errors = new ValidationErrors();
            errors = helper.getHttpParameters(errors);

            if (errors.getLength() == 0) {
                // No errors, save
                if (errors.getLength() == 0) {
                    content = cms.checkInContent(content, ContentStatus.PUBLISHED);
                }
                session.removeAttribute("currentContent");

                String url;
                String redirectUrl = param.getString("redirectUrl");
                if (redirectUrl != null && redirectUrl.length() > 0) {
                    url = redirectUrl;
                } else {
                    if (!content.hasDisplayTemplate()) {
                        // Has no display template, show parent
                        ContentIdentifier parentCid = cms.getParent(content.getContentIdentifier());
                        Content parent = cms.getContent(parentCid, false);
                        url = parent.getUrl();
                    } else {
                        url = content.getUrl();
                    }
                }

                session.removeAttribute("adminMode");

                return new ModelAndView(new RedirectView(url));
            } else {
                request.setAttribute("errors", errors);
                return showEditForm(request, content);
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
