package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCreateParameters;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractSimpleEditContentAction implements Controller {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleEditContentAction.class);

    private String view;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Autowired
    private ContentTemplateAO contentTemplateAO;

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
            log.error("Missing thisId and parentId parameter");
            throw new InvalidParameterException("Missing thisId and parentId parameter");
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SecuritySession securitySession = getSecuritySession(request);
        if (securitySession == null || !securitySession.isLoggedIn()) {
            throw new NotAuthorizedException("Not logged in");
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            // Save page
            return handleSubmit(request, response);
        } else {
            // Edit page
            Content content = getContentForEdit(request);
            if (isAllowedToEdit(request, content)) {
                ContentManagementService cms = new ContentManagementService(securitySession);
                if (!content.isNew()) {
                    // Existing content must be checked out before edit
                    content = cms.checkOutContent(content.getContentIdentifier());
                }
                request.setAttribute(AdminRequestParameters.MINI_ADMIN_MODE, true);
                return showEditForm(request, content);
            } else {
                throw new NotAuthorizedException("Not authorized to edit " + content.getId());
            }
        }
    }

    private Content createNewPage(HttpServletRequest request) throws InvalidFileException, InvalidTemplateException, NotAuthorizedException {
        ContentCreateParameters createParam = new ContentCreateParameters(request);
        return new ContentManagementService(request).createNewContent(createParam);
    }

    private Content getExistingPage(HttpServletRequest request) throws NotAuthorizedException, SystemException, InvalidFileException, InvalidTemplateException, ObjectLockedException, ContentNotFoundException {
        ContentIdentifier cid = contentIdHelper.fromRequest(request);
        return new ContentManagementService(request).getLastVersionOfContent(cid);
    }

    private ModelAndView showEditForm(HttpServletRequest request, Content content) throws ConfigurationException, InvalidFileException, ObjectLockedException, InvalidTemplateException, NotAuthorizedException {
        HttpSession session = request.getSession(true);

        RequestHelper.setRequestAttributes(request, content);

        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && redirectUrl.length() > 0) {
            request.setAttribute("redirectUrl", redirectUrl);
        }
        String draftRedirectUrl = request.getParameter("draftRedirectUrl");
        if (draftRedirectUrl != null && draftRedirectUrl.length() > 0) {
            request.setAttribute("draftRedirectUrl", draftRedirectUrl);
        }
        request.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);
        session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);

        Configuration config = Aksess.getConfiguration();
        Boolean allowArchive = Boolean.valueOf(config.getString("miniaksess.mediaarchive", "false"));
        request.setAttribute("miniAksessMediaArchive", allowArchive);

        ContentTemplate contentTemplate = contentTemplateAO.getTemplateById(content.getContentTemplateId());
        if (contentTemplate.isHearingEnabled() && content.getStatus() != ContentStatus.HEARING) {
            request.setAttribute("hearingEnabled", Boolean.TRUE);
        }


        addCustomRequestAttributes(request, content);

        return new ModelAndView(getView(), null);
    }

    private ModelAndView handleSubmit(HttpServletRequest request, HttpServletResponse response) throws InvalidFileException, InvalidTemplateException, NotAuthorizedException, ObjectLockedException, ConfigurationException {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);
        ContentManagementService cms = new ContentManagementService(getSecuritySession(request));

        Content content = (Content) session.getAttribute("currentContent");


        if (content != null) {
            // Page in session, save
            SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

            ValidationErrors errors = new ValidationErrors();
            errors = helper.getHttpParameters(errors);

            String addRepeaterRow = param.getString("addRepeaterRow");
            String deleteRepeaterRow = param.getString("deleteRepeaterRow");
            if (addRepeaterRow != null && addRepeaterRow.length() > 0) {
                addRepeaterRow(content, addRepeaterRow);
                request.setAttribute("scrollTo", getScrollTo(addRepeaterRow));
                return showEditForm(request, content);
            } else if (deleteRepeaterRow != null && deleteRepeaterRow.length() > 0) {
                deleteRepeaterRow(content, deleteRepeaterRow);
                request.setAttribute("scrollTo", getScrollTo(deleteRepeaterRow));
                return showEditForm(request, content);
            } else {
                if (errors.getLength() == 0) {
                    // No errors, save
                    int statusParam = param.getInt("status");
                    ContentStatus status = getContentStatus(statusParam);

                    content = cms.checkInContent(content, status);
                    session.removeAttribute("currentContent");

                    session.removeAttribute("adminMode");

                    return postSaveContent(request, response, content, content.isNew());
                } else {
                    request.setAttribute("errors", errors);
                    return showEditForm(request, content);
                }
            }
        }

        session.removeAttribute("adminMode");

        return new ModelAndView(new RedirectView(Aksess.getContextPath()));
    }

    private ContentStatus getContentStatus(int statusParam) {
        ContentStatus status;
        if (statusParam == -1) {
            status = ContentStatus.PUBLISHED;
        } else {
            status = ContentStatus.getContentStatusAsEnum(statusParam);
        }
        return status;
    }

    protected void addRepeaterRow(Content content, String addRepeaterRow) {
        try {
            EditContentHelper.addRepeaterRow(content, addRepeaterRow, AttributeDataType.CONTENT_DATA);
        } catch (InvalidTemplateException e) {
            throw new SystemException("Failed adding repeater rows", e);
        }
    }

    protected void deleteRepeaterRow(Content content, String addRepeaterRow) {
        EditContentHelper.deleteRepeaterRow(content, addRepeaterRow, AttributeDataType.CONTENT_DATA);
    }

    private String getScrollTo(String rowPath) {
        if (rowPath.contains("[")) {
            rowPath = rowPath.substring(0, rowPath.indexOf("["));
        }
        return AttributeHelper.getInputContainerName(rowPath);
    }

    protected void addCustomRequestAttributes(HttpServletRequest request, Content content) {

    }

    protected ModelAndView postSaveContent(HttpServletRequest request, HttpServletResponse response, Content content, boolean isNew) {
        String url;
        String redirectUrl = request.getParameter("redirectUrl");
        String draftRedirectUrl = request.getParameter("draftRedirectUrl");
        if (redirectUrl != null && redirectUrl.length() > 0) {
            url = redirectUrl;
        } else if (content.getStatus() == ContentStatus.DRAFT && draftRedirectUrl != null && draftRedirectUrl.length() > 0) {
            url = draftRedirectUrl;
        } else {
            if (!content.hasDisplayTemplate()) {
                ContentManagementService cms = new ContentManagementService(getSecuritySession(request));
                // Has no display template, show parent
                ContentIdentifier parentCid = cms.getParent(content.getContentIdentifier());
                Content parent = null;
                try {
                    parent = cms.getContent(parentCid, false);
                    url = parent.getUrl();
                } catch (NotAuthorizedException e) {
                    url = Aksess.getContextPath();
                }
            } else {
                url = content.getUrl();
            }
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
