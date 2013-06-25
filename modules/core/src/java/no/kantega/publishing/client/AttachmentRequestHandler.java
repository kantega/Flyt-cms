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

import com.yammer.metrics.annotation.Timed;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles requests for attachments.
 * Urlpatterns handled are:
 * - /attachment.ap?id=${id}
 * - /attachment/${id}/filename
 */
public class AttachmentRequestHandler extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AttachmentRequestHandler.class);

    private final Pattern urlPattern = Pattern.compile("/(\\d+)/.*");

    private SiteCache siteCache;

    @Timed
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        if(siteCache == null){
            siteCache = RootContext.getInstance().getBean(SiteCache.class);
        }
        try {
            ContentManagementService cs = new ContentManagementService(request);

            int attachmentId = -1;

            String id = (String)request.getAttribute("attachment-id");
            if (id != null) {
                try {
                    attachmentId = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    log.error( "Attachment request from ContentRequestDispatcher contained non parsable attachment-id: " + id);
                }
            } else {
                attachmentId = param.getInt("id");
                String info = request.getPathInfo();
                if(attachmentId == -1 && info != null) {
                    try {
                        Matcher matcher = urlPattern.matcher(info);
                        if (matcher.matches()){
                            attachmentId = Integer.parseInt(matcher.group(1));
                        }
                    } catch (NumberFormatException e) {
                        log.error( "Invalid attachment request " + info);
                    }
                }
            }

            if (attachmentId == -1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

            String anchor = param.getString("anchor");

            int siteId = -1;

            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }

            Attachment attachment = cs.getAttachment(attachmentId, siteId);
            if (attachment == null) {
                // Attachment not found
                log.error( "Attachment not found. Attachment id requested: " + attachmentId + " on siteId: " + siteId);
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

            if (HttpHelper.isInClientCache(request, response, String.valueOf(attachmentId), attachment.getLastModified())) {
                // Found in browser cache
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            // Add cache control headers
            Configuration config = Aksess.getConfiguration();
            int expire = config.getInt("attachments.expire", -1);
            HttpHelper.addCacheControlHeaders(response, expire);
            
            try (ServletOutputStream out = response.getOutputStream()){
                if (attachment.getSize() > 0) {
 	  	            response.addHeader("Content-Length", String.valueOf(attachment.getSize()));
                }
                log.info("Sending attachment {}", attachmentId);
                cs.streamAttachmentData(attachmentId, new InputStreamHandler(out));
                out.flush();
            } catch (Exception e) {
                // Client has aborted / connection closed
            }
        } catch (Exception e) {
            log.error("Error streaming attachment", e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}

