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

package no.kantega.publishing.security.login;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.security.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public class PostLoginHandlerFactory implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(PostLoginHandlerFactory.class);
    private Collection<PostLoginHandler> postLoginHandlers;


    public PostLoginHandler newInstance() throws SystemException, ConfigurationException {
        return new PostLoginHandler() {
            @Override
            public void handlePostLogin(User user, HttpServletRequest request) throws SystemException {
                for (PostLoginHandler postLoginHandler : postLoginHandlers) {
                    postLoginHandler.handlePostLogin(user, request);
                }
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.postLoginHandlers = applicationContext.getBeansOfType(PostLoginHandler.class).values();
    }
}
