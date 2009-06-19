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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class AddAttachmentAction extends HttpServlet {
    private static String SOURCE = "aksess.DeleteAttachmentAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestParameters param = new RequestParameters(request, "utf-8");

        HttpSession session = request.getSession();
        Content content = (Content)session.getAttribute("currentContent");

        try {
            ContentManagementService aksessService = new ContentManagementService(request);
            
            int attachmentId   = param.getInt("attachmentId");
            boolean insertLink = param.getBoolean("insertLink");

            MultipartFile file = param.getFile("attachment");
            if (file != null && content != null) {
                Attachment attachment = new Attachment();
                attachment.setContentId(content.getId());
                attachment.setLanguage(content.getLanguage());
                attachment.setId(attachmentId);

                byte[] data = file.getBytes();

                String filename = file.getOriginalFilename();
                if (filename.length() > 255) {
                    filename = filename.substring(filename.length() - 255, filename.length());
                }

                attachment.setFilename(filename);
                attachment.setData(data);
                attachment.setSize(data.length);

                attachmentId = aksessService.setAttachment(attachment);

                attachment.setId(attachmentId);
                if (content.getId() <= 0) {
                    // Legger til vedlegg i liste, disse oppdateres med riktig contentid ved lagring
                    // Evt slettes ved avbryt
                    attachment.setData(null);
                    content.addAttachment(attachment);
                    session.setAttribute("currentContent", content);
                }
            }
            response.sendRedirect("attachmentconfirm.jsp?attachmentId=" + attachmentId + "&insertLink=" + insertLink + "&refresh=" + new Date().getTime());
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);            

            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }
}


