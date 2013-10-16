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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles requests for attachments.
 * Urlpatterns handled are:
 * - /attachment.ap?id=${id}
 * - /attachment/${id}/filename
 */
@Controller
public abstract class AttachmentRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(AttachmentRequestHandler.class);

    @Autowired
    private SiteCache siteCache;
    private int expire;

    @PostConstruct
    public void init(){
        Configuration config = Aksess.getConfiguration();
        expire = config.getInt("attachments.expire", -1);
    }

    @RequestMapping("/attachment/{id:[0-9]+}/*")
    public void handleAttachment(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        doHandleAttachment(id, request, response);
    }

    @RequestMapping("/attachment.ap")
    public void handleAttachment_Ap(@RequestParam(required = false, defaultValue = "-1") int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        id = tryGetFromRequestAttribute(id, request);
        doHandleAttachment(id, request, response);
    }

    private int tryGetFromRequestAttribute(int id, HttpServletRequest request) {
        if (id == -1) {
            String idAttribute = (String)request.getAttribute("attachment-id");
            try {
                id = Integer.parseInt(idAttribute);
            } catch (NumberFormatException e) {
                log.error( "Attachment request from ContentRequestDispatcher contained non parsable attachment-id: " + id);
            }
        }
        return id;
    }

    private void doHandleAttachment(int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id == -1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int siteId = -1;

        Site site = siteCache.getSiteByHostname(request.getServerName());
        if (site != null) {
            siteId = site.getId();
        }
        Attachment attachment;
        ContentManagementService cs = new ContentManagementService(getSecuritySession());
        try {
            attachment = cs.getAttachment(id, siteId);
        } catch (NotAuthorizedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (attachment == null) {
            // Attachment not found
            log.error( "Attachment not found. Attachment id requested: " + id + " on siteId: " + siteId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimetype = attachment.getMimeType().getType();
        response.setContentType(mimetype);
        String filename = attachment.getFilename();
        if (request.getParameter("anchor") == null) {
            // Dont send filename if anchor is used, gives problem in browser with filename
            String contentDisposition = request.getParameter("contentdisposition");

            contentDisposition = "inline".equals(contentDisposition) ? "inline" : "attachment";
            response.addHeader("Content-Disposition", contentDisposition +"; filename=\"" + filename + "\"");
        }

        if (HttpHelper.isInClientCache(request, response, String.valueOf(id), attachment.getLastModified())) {
            // Found in browser cache
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // Add cache control headers
        HttpHelper.addCacheControlHeaders(response, expire);

        try (ServletOutputStream out = response.getOutputStream()){
            if (attachment.getSize() > 0) {
                response.addHeader("Content-Length", String.valueOf(attachment.getSize()));
            }
            log.info("Sending attachment {}", attachment.getFilename());
            cs.streamAttachmentData(id, new InputStreamHandler(out));
            out.flush();
        } catch (Exception e) {
            // Client has aborted / connection closed
        }
    }

    protected abstract SecuritySession getSecuritySession();
}

