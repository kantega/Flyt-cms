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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.topicmaps.data.Topic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

public class GetUserTag  extends TagSupport {
    private static final String SOURCE = "aksess.GetUserTag";

    private String name = "currentuser";
    private String userid = null;
    private boolean getRoles = false;
    private boolean getRoleTopics = false;
    private boolean useCache = true;

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        try {
            User user = null;

            SecuritySession session = SecuritySession.getInstance(request);
            if(userid != null) {
                SecurityRealm realm = SecurityRealmFactory.getInstance();
                try {
                    user = realm.lookupUser(userid, useCache);

                    if (user != null) {
                        if (getRoles || getRoleTopics) {
                            List roles = realm.lookupRolesForUser(user.getId());
                            for (int i = 0; i < roles.size(); i++) {
                                Role role =  (Role)roles.get(i);
                                user.addRole(role);
                            }
                            if (getRoleTopics && Aksess.isTopicMapsEnabled()) {
                                // Hent topics for bruker
                                TopicMapService topicService = new TopicMapService(request);

                                if (user.getRoles() != null) {
                                    for (int i = 0; i < roles.size(); i++) {
                                        Role role =  (Role)roles.get(i);
                                        List tmp = topicService.getTopicsBySID(role);
                                        for (int j = 0; j < tmp.size(); j++) {
                                            user.addTopic((Topic)tmp.get(j));
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
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        userid = null;
        name = "currentuser";
        useCache = true;
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

    public void setUsecache(boolean useCache) {
        this.useCache = useCache;
    }
}


