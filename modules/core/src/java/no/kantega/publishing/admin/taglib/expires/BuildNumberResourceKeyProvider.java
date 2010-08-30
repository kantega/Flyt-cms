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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Returns an MD5 hash of the build number (SCM revision) or current time stamp if in development mode.
 */
public class BuildNumberResourceKeyProvider implements ResourceKeyProvider, InitializingBean {

    private RuntimeMode runtimeMode;
    private String key;

    public String getUniqueKey(HttpServletRequest request, HttpServletResponse response, String url) {
        if (runtimeMode == RuntimeMode.DEVELOPMENT) {
            return Long.toString(System.currentTimeMillis());
        } else {
            return key;
        }
    }

    public void afterPropertiesSet() throws Exception {
        try {
            MessageDigest dig = MessageDigest.getInstance("MD5");
            dig.update(Aksess.getWebappRevision().getBytes("utf-8"));
            key = DigestPrettyPrinter.prettyPrintDigest(dig.digest()).substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRuntimeMode(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }
}
