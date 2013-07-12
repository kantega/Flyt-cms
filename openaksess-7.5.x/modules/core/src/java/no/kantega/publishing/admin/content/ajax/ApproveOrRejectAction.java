/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ApproveOrRejectAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        boolean approve = param.getBoolean("approve", false);
        boolean reject = param.getBoolean("reject", false);

        String note = param.getString("note", 2000);

        HttpSession session = request.getSession(true);

        ContentIdentifier cid = ContentIdHelper.fromRequestAndUrl(request, request.getParameter("url"));

        ContentManagementService aksessService = new ContentManagementService(request);

        if (approve || reject) {
            ContentStatus status = approve ? ContentStatus.PUBLISHED : ContentStatus.REJECTED;
            Content content = aksessService.setContentStatus(cid, status, note);
            Content currentNavigateContent = (Content) session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
            if (currentNavigateContent != null && currentNavigateContent.getId() == content.getId()) {
                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, content);
            }
            if (status == ContentStatus.REJECTED) {
                sendRejectMail(content, note);
            }
        }

        return null;
    }

    private void sendRejectMail(Content content, String note) throws ConfigurationException {
        String from = Aksess.getConfiguration().getString("mail.from", "noreply");
        String to = getEmailForUser(content.getModifiedBy());

        String contenturl = Aksess.getApplicationUrl() + "/admin/publish/Navigate.action?thisId=" + content.getAssociation().getAssociationId();
        String title = content.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = LocaleLabels.getLabel("aksess.reject.notitle", Aksess.getDefaultLocale());
        }

        Map<String, Object> param = new HashMap<>();
        param.put("contenturl", contenturl);
        param.put("title", title);
        param.put("note", HtmlUtils.htmlEscape(note));
        param.put("editor", Aksess.getConfiguration().getString("mail.editor", "noreply"));

        String messageBody = MailSender.createStringFromVelocityTemplate("contentrejected.vm", param);
        String subject = LocaleLabels.getLabel("aksess.reject.mailsubject", Aksess.getDefaultLocale());
        if (StringUtils.isNotBlank(to)) {
            MailSender.send(from, to, subject, messageBody);
        }
    }

    // Null-safe lookup of email for userid.
    private String getEmailForUser(String userid) {
        String email = "";
        if (StringUtils.isNotBlank(userid)) {
            SecurityRealm realm = SecurityRealmFactory.getInstance();
            if (realm != null) {
                User user = realm.lookupUser(userid);
                if (user != null) {
                    email = user.getEmail();
                }
            }
        }
        return email;
    }
}
