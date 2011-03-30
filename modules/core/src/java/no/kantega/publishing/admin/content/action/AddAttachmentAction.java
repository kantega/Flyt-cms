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
import no.kantega.commons.media.MimeType;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.admin.AdminSessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import no.kantega.publishing.admin.content.util.AttachmentBlacklistHelper;

public class AddAttachmentAction implements Controller {

    private String formView;
    
    private String confirmView;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters requestParameters = new RequestParameters(request, "utf-8");

        HttpSession session = request.getSession();
        Content content = (Content) session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        int attachmentId = requestParameters.getInt("attachmentId");
        boolean insertLink = requestParameters.getBoolean("insertLink");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("refresh", new Date().getTime());
        model.put("insertlink", insertLink);

        if (request.getMethod().equalsIgnoreCase("POST")) {
            ContentManagementService aksessService = new ContentManagementService(request);

            MultipartFile multipartFile = requestParameters.getFile("attachment");

            // Cancel if the file type is blacklisted
            if (AttachmentBlacklistHelper.isFileTypeInBlacklist(multipartFile)) {
                return new ModelAndView(confirmView, model);
            }

            if ((multipartFile != null) && (content != null)) {
                Attachment attachment = new Attachment();
                attachment.setContentId(content.getId());
                attachment.setLanguage(content.getLanguage());
                attachment.setId(attachmentId);

                byte[] data = multipartFile.getBytes();

                String filename = multipartFile.getOriginalFilename();
                if (filename.length() > 255) {
                    filename = filename.substring(filename.length() - 255, filename.length());
                }

                attachment.setFilename(filename);
                attachment.setData(data);
                attachment.setSize(data.length);

                attachmentId = aksessService.setAttachment(attachment);

                attachment.setId(attachmentId);
                if (content.isNew()) {
                    // Add attachments to list, these are updated with a correct contentid when saved in datadata
                    // Or deleted if user cancels
                    attachment.setData(null);
                    content.addAttachment(attachment);
                    session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);
                }
                model.put("attachmentId", attachmentId);
                model.put("name", filename);
                MimeType mimeType = attachment.getMimeType();
                if (mimeType != null) {
                    model.put("mimeType", mimeType.getType());
                    model.put("fileExtension", mimeType.getFileExtension());
                }
            }
            return new ModelAndView(confirmView, model);
        } else {
            if (attachmentId != -1) {
                model.put("attachmentId", attachmentId);
            }
            model.put("blacklistedFileTypes", AttachmentBlacklistHelper.getBlacklistedFileTypes());
            model.put("blacklistedErrorMessage", AttachmentBlacklistHelper.getErrorMessage());
            return new ModelAndView(formView, model);
        }
    }

    public String getFormView() {
        return formView;
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public String getConfirmView() {
        return confirmView;
    }

    public void setConfirmView(String confirmView) {
        this.confirmView = confirmView;
    }
}
