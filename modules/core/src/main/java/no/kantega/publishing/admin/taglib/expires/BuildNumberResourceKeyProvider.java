/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.taglib.expires;

import no.kantega.commons.taglib.expires.DigestPrettyPrinter;
import no.kantega.commons.taglib.expires.ResourceKeyProvider;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.RuntimeMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Returns an MD5 hash of the build number (SCM revision) or current time stamp if in development mode.
 */
public class BuildNumberResourceKeyProvider implements ResourceKeyProvider, InitializingBean, ServletContextAware {

    private RuntimeMode runtimeMode;
    private String key;
    private ServletContext servletContext;

    public String getUniqueKey(HttpServletRequest request, String url) {
        if (runtimeMode == RuntimeMode.DEVELOPMENT) {
            try {
                // Try as servlet context resource first
                URL resource = servletContext.getResource(url);
                if(resource != null) {
                    return Long.toString(resource.openConnection().getLastModified());
                } else {
                    return Long.toString(System.currentTimeMillis());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            return key;
        }
    }

    public void afterPropertiesSet() throws Exception {
        try {
            String versjonString = Aksess.getWebappVersion() + Aksess.getWebappRevision() + Aksess.getWebappDate();
            MessageDigest dig = MessageDigest.getInstance("MD5");
            dig.update(versjonString.getBytes("utf-8"));
            key = DigestPrettyPrinter.prettyPrintDigest(dig.digest()).substring(0, 10);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRuntimeMode(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
