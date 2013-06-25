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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostLoginHandlerFactory {
    private static final Logger log = LoggerFactory.getLogger(PostLoginHandlerFactory.class);
    private static final String SOURCE = "aksess.PostLoginHandlerFactory";


    public static PostLoginHandler newInstance () throws SystemException, ConfigurationException {
        Configuration c = Aksess.getConfiguration();

        String postloginhandler = c.getString("security.postloginhandler");
        if (postloginhandler == null || postloginhandler.length() == 0) {
            return null;
        }

        try {
            return (PostLoginHandler)Class.forName(postloginhandler).newInstance();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

}
