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

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.security.data.User;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;

public class GetUserNameTag extends TagSupport {
    private static final String SOURCE = "aksess.GetUserNameTag";

    private String userid;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        JspWriter out;
        try {
            User user = null;

            SecuritySession session = SecuritySession.getInstance(request);
            if (userid != null) {
                SecurityRealm realm = SecurityRealmFactory.getInstance();
                try {
                    user = realm.lookupUser(userid);
                } catch (SystemException e) {
                    user = null;
                }
            } else {
                user = session.getUser();
            }

            out = pageContext.getOut();
            if (user != null) {
                out.write(user.getName());
            } else if (userid != null) {
                out.write(userid);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
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
}

