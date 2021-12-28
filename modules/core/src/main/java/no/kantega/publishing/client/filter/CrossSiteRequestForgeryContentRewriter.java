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

package no.kantega.publishing.client.filter;

import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossSiteRequestForgeryContentRewriter implements ContentRewriter{
    private Pattern pattern = Pattern.compile("<(form|FORM)[^>]*>");

    private BigInteger secret = new BigInteger(128, new SecureRandom());
    public  static final String CSRF_KEY = "csrfkey";

    public String rewriteContent(HttpServletRequest request, String content) {
        String csrfkeyDiv = "<div style=\"display: none\"><input type=\"hidden\" name=\"" + CSRF_KEY + "\" value=\"" + (generateKey(request)) + ("\"></div>");
        if( !shouldRewrite(request) || content.contains(csrfkeyDiv)) {
            return content;
        }
        StringBuilder builder  = new StringBuilder();
        final Matcher matcher = pattern.matcher(content);

        int prev = 0;
        while(matcher.find()) {
            builder.append(content.substring(prev, matcher.end()));
            builder.append(csrfkeyDiv);
            prev = matcher.end();
        }
        if(prev < content.length()) {
            builder.append(content.substring(prev));
        }
        return builder.toString();
    }

    protected boolean shouldRewrite(HttpServletRequest request) {
        return HttpHelper.isAdminMode(request);
    }

    protected String generateKey(HttpServletRequest request) {
        try {
            return new BigInteger(request.getSession().getId().getBytes("utf8")).xor(secret).toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getSecret() {
        return secret;
    }
}
