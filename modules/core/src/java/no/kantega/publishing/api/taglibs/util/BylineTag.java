/*
 * Copyright 2014 Kantega AS
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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BylineTag extends BodyTagSupport {
    private static final Logger log = LoggerFactory.getLogger(BylineTag.class);
    private static ContentIdHelper contentIdHelper;

    private String cssStyle = null;
    private String cssClass = null;

    private String key = null;
    private String bundle = LocaleLabels.DEFAULT_BUNDLE;
    private String locale = null;


    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setCssclass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        try {
            JspWriter out = bodyContent.getEnclosingWriter();

            Content content = content(pageContext);

            if (content != null) {

                String userFullName = userFullName(content);

                Date lastUpdated = content.getLastMajorChange();

                String textLabel = textLabel(pageContext, key, bundle, locale, bodyContent.toString());

                String text = String.format(textLabel, userFullName, lastUpdated);

                String html = html(text);

                out.print(html);
            }

        } catch (Exception e) {
            log.error("Failed while generating byline", e);
            throw new JspTagException(e);
        } finally {
            bodyContent.clearBody();
        }

        cssStyle = null;
        cssClass = null;
        key = null;
        bundle = LocaleLabels.DEFAULT_BUNDLE;
        locale = null;

        return SKIP_BODY;
    }

    private static String textLabel(PageContext pageContext, String key, String bundle, String locale, String body) {
        String textLabel;
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        if (key != null) {
            textLabel = LocaleLabels.getLabel(key, bundle, locale(request, locale));
        } else {
            textLabel = body;
        }
        return textLabel;
    }

    private String html(String text) {
        StringBuilder b = new StringBuilder();

        b.append("<div");
        if (cssStyle != null) {
            b.append(" style=\"").append(cssStyle).append("\"");
        }
        if (cssClass != null) {
            b.append(" class=\"").append(cssClass).append("\"");
        }
        b.append(">").append(text).append("</div>");

        return b.toString();
    }

    private static Content content(PageContext pageContext) throws NotAuthorizedException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            Content content = (Content) request.getAttribute("aksess_this");
            if (content == null) {
                if (contentIdHelper == null) {
                    contentIdHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean(ContentIdHelper.class);
                }
                ContentIdentifier ci = contentIdHelper.fromRequest(request);
                content = new ContentManagementService(request).getContent(ci, true);
                RequestHelper.setRequestAttributes(request, content);
            }
            return content;
        } catch (ContentNotFoundException e) {
            return null; // Not an error
        }
    }

    private static String userFullName(Content current) {
        String userid = current.getPublisher();
        User user = null;
        if (!isBlank(userid)) {
            user = userFromSecurityRealm(userid);
        }
        return user != null ? user.getName() : (userid != null ? userid : "");
    }

    private static User userFromSecurityRealm(String userid) {
        try {
            SecurityRealm realm = SecurityRealmFactory.getInstance();
            return realm.lookupUser(userid);
        } catch (SystemException e) {
            return null;
        }
    }

    private static Locale locale(HttpServletRequest request, String localeString) {
        Locale locale;
        if (isNotBlank(localeString)){
            String[] localePair = localeString.split("_");
            locale = new Locale(localePair[0], localePair[1]);
        } else {
            locale = LocaleLabels.getLocaleFromRequestOrDefault(request);
        }
        return locale;
    }

}
