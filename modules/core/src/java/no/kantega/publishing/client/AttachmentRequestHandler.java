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

package no.kantega.publishing.client;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.commons.configuration.Configuration;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class AttachmentRequestHandler extends HttpServlet {
    private static String SOURCE = "aksess.AttachmentRequestHandler";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        try {
            ContentManagementService cs = new ContentManagementService(request);

            int attachmentId = -1;

            String id = (String)request.getAttribute("attachment-id");
            if (id != null) {
                try {
                    attachmentId = Integer.parseInt(id);
                } catch (NumberFormatException e) {

                }
            } else {
                attachmentId = param.getInt("id");
                if(attachmentId == -1) {
                    try {
                        String info = request.getPathInfo();
                        attachmentId = Integer.parseInt(info.substring(1, info.indexOf(".", 1)));
                    } catch (Exception e) {

                    }
                }
            }

            if (attachmentId == -1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

            String anchor = param.getString("anchor");

            int siteId = -1;
            Site site = SiteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }

            Attachment attachment = cs.getAttachment(attachmentId, siteId);
            if (attachment == null) {
                // Attachment not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String mimetype = attachment.getMimeType().getType();
            response.setContentType(mimetype);
            String filename = attachment.getFilename();
            if (anchor == null) {
                // Dont send filename if anchor is used, gives problem in browser with filename
                String contentDisposition = param.getString("contentdisposition");
                
                contentDisposition = "inline".equals(contentDisposition) ? "inline" : "attachment";
                response.addHeader("Content-Disposition", contentDisposition +"; filename=\"" + filename + "\"");
            }

            if (HttpHelper.isInClientCache(request, response, "" + attachmentId, attachment.getLastModified())) {
                // Found in browser cache
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            // Add cache control headers
            Configuration config = Aksess.getConfiguration();
            int expire = config.getInt("attachments.expire", -1);
            HttpHelper.addCacheControlHeaders(response, expire);
            
            ServletOutputStream out = response.getOutputStream();

            try {
                if (attachment.getSize() > 0) {
 	  	            response.addHeader("Content-Length", "" + attachment.getSize());
                }
                cs.streamAttachmentData(attachmentId, new InputStreamHandler(out));
                out.flush();
                out.close();
            } catch (Exception e) {
                // Client has aborted / connection closed
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

