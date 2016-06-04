/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.admin.content.util.ContentAliasValidator;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.MultipleEditorInstancesException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSaveContentAction extends AbstractContentAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractSaveContentAction.class);

    abstract ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException;
    abstract String getView();
    abstract Map<String, Object> getModel(Content content, HttpServletRequest request);
    private boolean updatePublishProperties = true;

    @Autowired
    private ContentAO contentAO;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService aksessService = new ContentManagementService(request);

        HttpSession session = request.getSession();
        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        log.debug("Content on start: {}");

        if (content == null) {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }
        // Form data is always UTF-8
        RequestParameters param = new RequestParameters(request, "utf-8");

        Map<String, Object> model = getModel(content, request);

        if (request.getMethod().equalsIgnoreCase("POST")) {
            log.info("Submit from user on content " + content.getTitle() + " (" + content.getId() + ", " + content.getVisibilityStatus()+ ", " + content.getStatus() +")");

            boolean isModified = param.getBoolean("isModified");

            // Id of page being edited, checked towards session to prevent problems with multiple edits at the same time
            int currentId = param.getInt("currentId");

            if (currentId != content.getId()) {
                throw new MultipleEditorInstancesException();
            }

            //  This flag is only a signal for user that content is modified, content is always saved when user requests a save
            content.setIsModified(isModified);

            ContentStatus status = ContentStatus.getContentStatusAsEnum(param.getInt("status"));
            String action = param.getString("action");

            ValidationErrors errors = updateSubmittedValues(aksessService, content, param);
            log.info("Updated {}, errors: {}", content, errors);
            String addRepeaterRow = param.getString("addRepeaterRow");
            String deleteRepeaterRow = param.getString("deleteRepeaterRow");
            if (addRepeaterRow != null && addRepeaterRow.length() > 0) {
                addRepeaterRow(content, addRepeaterRow);
                model.put("scrollTo", getScrollTo(addRepeaterRow));
                return goBackToEditPage(request, aksessService, content, model);
            } else if (deleteRepeaterRow != null && deleteRepeaterRow.length() > 0) {
                deleteRepeaterRow(content, deleteRepeaterRow);
                model.put("scrollTo", getScrollTo(deleteRepeaterRow));
                return goBackToEditPage(request, aksessService, content, model);
            } else {
                return handleSaveFromUser(request, aksessService, session, content, model, status, action, errors);
            }

        } else {
            log.debug("Get from user");
            return goBackToEditPage(request, aksessService, content, model);
        }
    }

    private String getScrollTo(String rowPath) {
        if (rowPath.contains("[")) {
            rowPath = rowPath.substring(0, rowPath.indexOf('['));
        }
        return AttributeHelper.getInputContainerName(rowPath);
    }


    private ValidationErrors updateSubmittedValues(ContentManagementService aksessService, Content content, RequestParameters param) throws InvalidFileException, InvalidTemplateException{
        ValidationErrors errors = new ValidationErrors();

        if (updatePublishProperties) {
            // Get publish information, must be done first
            errors.addAll(savePublishProperties(content, param, aksessService));
        }

        // Get tab specific parameters
        errors.addAll(saveRequestParameters(content, param, aksessService));
        return errors;
    }

    private ModelAndView handleSaveFromUser(HttpServletRequest request, ContentManagementService aksessService, HttpSession session, Content content, Map<String, Object> model, ContentStatus status, String action, ValidationErrors errors) throws NotAuthorizedException {
        if (errors.getLength() > 0) {
            return showErrorsToUser(request, aksessService, content, model, errors);
        } else {
            if (status != null && errors.getLength() == 0) {
                return saveContentInDb(aksessService, session, content, model, status);
            }
            session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);
            if (action == null || action.length() == 0) {
                // Go back to current tab
                return goBackToEditPage(request, aksessService, content, model);
            } else {
                // Go to another tab
                return new ModelAndView(new RedirectView(action));
            }
        }
    }


    protected void addRepeaterRow(Content content, String addRepeaterRow) {
        // Does nothing as default, but can be overriden
    }

    protected void deleteRepeaterRow(Content content, String deleteRepeaterRow) {
        // Does nothing as default, but can be overriden
    }


    private ModelAndView goBackToEditPage(HttpServletRequest request, ContentManagementService aksessService, Content content, Map<String, Object> model) {
        // No submit
        model.put("isEditing", Boolean.TRUE);
        setRequestVariables(request, content, aksessService, model);
        return new ModelAndView(getView(), model);
    }


    private ModelAndView saveContentInDb(ContentManagementService aksessService, HttpSession session, Content content, Map<String, Object> model, ContentStatus status) throws NotAuthorizedException {
        log.info("Saving content {} with status", content, status);
        String message;
        content = aksessService.checkInContent(content, status);
        message = null;
        status = content.getStatus();
        switch (status) {
            case DRAFT:
            case GHOSTDRAFT:
                message = "draft";
                break;
            case PUBLISHED:
                message = "published";
                break;
            case WAITING_FOR_APPROVAL:
                message = "waiting";
                break;
            case HEARING:
                message = "hearing";
                break;
        }
        model.put("message", message);

        if (!content.hasDisplayTemplate()) {
            // Go to parent page when finished for files, annoying to get a file download dialogue
            ContentIdentifier parentCid = aksessService.getParent(content.getContentIdentifier());
            Content parent = aksessService.getContent(parentCid, false);
            if (parent != null) {
                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, parent);
            }
        } else {
            // Go to this page when finished
            session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, content);
        }
        return new ModelAndView(new RedirectView("Navigate.action"));
    }


    private ModelAndView showErrorsToUser(HttpServletRequest request, ContentManagementService aksessService, Content content, Map<String, Object> model, ValidationErrors errors) {
        // Error on page, send user back to correct error
        model.put("errors", errors);
        return goBackToEditPage(request, aksessService, content, model);
    }


    private ValidationErrors savePublishProperties(Content content, RequestParameters param, ContentManagementService aksessService) {
        ValidationErrors errors = new ValidationErrors();

        if (dateFieldExists(param, "from")) {
            try {
                Date startDate = param.getDateAndTime("from", Aksess.getDefaultDateFormat());
                content.setPublishDate(startDate);
                if (startDate == null && (!content.isNew()) && contentAO.hasBeenPublished(content.getId())) {
                    errors.add(null, "aksess.error.publishdatenotset");
                }
            } catch(Exception e) {
                Map<String, Object> objects = new HashMap<>();
                objects.put("dateFormat", Aksess.getDefaultDateFormat());
                errors.add(null, "aksess.error.date", objects);
            }
        }

        if (dateFieldExists(param, "end")) {
            try {
                Date expireDate = param.getDateAndTime("end", Aksess.getDefaultDateFormat());
                content.setExpireDate(expireDate);
            } catch (Exception e) {
                Map<String, Object> objects = new HashMap<>();
                objects.put("dateFormat", Aksess.getDefaultDateFormat());
                errors.add(null, "aksess.error.date", objects);
            }
        }

        if (content.getPublishDate() != null && content.getExpireDate() != null) {
            if (content.getExpireDate().getTime() < content.getPublishDate().getTime()) {
                errors.add(null, "aksess.error.expirebeforepublish");
            }
        }

        try {
            Date changeDate = param.getDateAndTime("change", Aksess.getDefaultDateFormat());
            content.setChangeFromDate(changeDate);
        } catch (Exception e) {
            Map<String, Object> objects = new HashMap<>();
            objects.put("dateFormat", Aksess.getDefaultDateFormat());
            errors.add(null, "aksess.error.date", objects);
        }

        String expireAction = param.getString("expireaction");
        if (expireAction != null) {
            content.setExpireAction(ExpireAction.valueOf(expireAction));
        }

        if (aksessService.getSecuritySession().isUserInRole(Aksess.getDeveloperRole())) {
            content.setLocked(param.getBoolean("locked"));
        }

        setAlias(content, param, errors);

        if (aksessService.getSecuritySession().isUserInRole(Aksess.getDeveloperRole())) {
            content.setLocked(param.getBoolean("locked"));
        }

        content.setSearchable(param.getBoolean("searchable"));
        content.setMinorChange(param.getBoolean("minorchange", false));

        updateDisplayTemplateIfChanged(content, param, aksessService);

        return errors;
    }

    private void setAlias(Content content, RequestParameters param, ValidationErrors errors) {
        String alias = param.getString("alias", 255);
        if (alias != null) {
            content.setAlias(alias);
            if (alias.length() > 0) {
                // If alias contains . or = should not be modififed for historic reasons
                if (!alias.contains(".") && !alias.contains("=")) {
                    if (alias.charAt(0) != '/') {
                        alias = "/" + alias;
                    }
                    if (alias.length() > 1) {
                        if (alias.charAt(alias.length()-1) != '/') {
                            alias = alias + "/";
                        }
                    }
                    alias = alias.toLowerCase();

                    content.setAlias(alias);
                }

                ContentAliasValidator.validateAlias(alias, content, errors);
            }
        }
    }

    private void updateDisplayTemplateIfChanged(Content content, RequestParameters param, ContentManagementService aksessService) {
        int templateId = param.getInt("displaytemplate");
        if (templateId != -1) {
            if (templateId != content.getDisplayTemplateId()) {
                DisplayTemplate template = aksessService.getDisplayTemplate(templateId);
                if (template != null) {
                    content.setDisplayTemplateId(templateId);
                    content.setContentTemplateId(template.getContentTemplate().getId());
                    ContentTemplate mt = template.getMetaDataTemplate();
                    if (mt != null) {
                        content.setMetaDataTemplateId(mt.getId());
                    } else {
                        content.setMetaDataTemplateId(-1);
                    }
                    if (!content.isNew()) {
                        // Update group id
                        if (template.isNewGroup()) {
                            content.setGroupId(content.getId());
                        } else {
                            ContentIdentifier parentCid = aksessService.getParent(content.getContentIdentifier());
                            Content parent = null;
                            try {
                                parent = aksessService.getContent(parentCid);
                            } catch (NotAuthorizedException e) {
                                log.error( "Could not get parent for " + content.getTitle() + "(" + content.getId() + ")");
                            }
                            if (parent != null) {
                                content.setGroupId(parent.getGroupId());
                            }
                        }

                    }

                }
            }
        }
    }

    private boolean dateFieldExists(RequestParameters param, String field) {
        return param.getString(field + "_date") != null;
    }

    public void setUpdatePublishProperties(boolean updatePublishProperties) {
        this.updatePublishProperties = updatePublishProperties;
    }
}
