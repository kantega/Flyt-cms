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

package no.kantega.publishing.api.taglibs.security;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class GetUserNameTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetUserNameTag.class);
    private static WebApplicationContext webApplicationContext;

    private String userid;
    private boolean useCache;

    public int doStartTag() throws JspException {
        try {

            if (webApplicationContext == null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            }
            SecuritySession session = webApplicationContext.getBean(SecuritySession.class);
            User user;
            if (!isBlank(userid)) {
                SecurityRealm realm = SecurityRealmFactory.getInstance();
                try {
                    user = realm.lookupUser(userid, useCache);
                } catch (SystemException e) {
                    user = null;
                }
            } else {
                user = session.getUser();
            }

            JspWriter out = pageContext.getOut();
            if (user != null) {
                out.write(user.getName());
            } else if (userid != null) {
                out.write(userid);
            }
        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        userid = null;
        return EVAL_PAGE;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsecache(boolean useCache) {
        this.useCache = useCache;
    }
}

