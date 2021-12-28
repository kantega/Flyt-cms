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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class GetUserTag  extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetUserTag.class);
    private static WebApplicationContext webApplicationContext;

    private String name = "currentuser";
    private String userid = null;
    private boolean getRoles = false;
    private boolean getRoleTopics = false;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        try {
            if (webApplicationContext == null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            }
            SecuritySession session = SecuritySession.getInstance(request);
            User user = null;
            if(!isBlank(userid)) {
                try {
                    SecurityRealm realm = null;
                    Map<String,SecurityRealm> beansOfType = webApplicationContext.getBeansOfType(SecurityRealm.class);
                    for (SecurityRealm r : beansOfType.values()) {
                        user = r.lookupUser(userid);
                        if(user != null){
                            realm = r;
                            break;

                        }
                    }

                    if (user != null) {
                        if (getRoles || getRoleTopics) {
                            List<Role> roles = realm.lookupRolesForUser(user.getId());
                            for (Role role : roles) {
                                user.addRole(role);
                            }
                            if (getRoleTopics && Aksess.isTopicMapsEnabled()) {
                                // Hent topics for bruker
                                TopicMapService topicService = new TopicMapService(request);

                                if (user.getRoles() != null) {
                                    for (Role role : roles) {
                                        List<Topic> tmp = topicService.getTopicsBySID(role);
                                        for (Topic aTmp : tmp) {
                                            user.addTopic(aTmp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (SystemException e) {
                    user = null;
                }
            } else {
                user = session.getUser();
            }

            if (user != null) {
                request.setAttribute(name, user);
            }
        } catch (Exception e) {
            log.error("Error setting user", e);
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        userid = null;
        name = "currentuser";
        return EVAL_PAGE;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGetroles(String getRoles) {
        if ("true".equalsIgnoreCase(getRoles)) {
            this.getRoles = true;
        }
    }

    public void setGetroletopics(boolean getTopics) {
        this.getRoleTopics = getTopics;
    }
}


