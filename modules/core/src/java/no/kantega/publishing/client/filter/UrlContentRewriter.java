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
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.PrettyURLEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.CharArrayWriter;
import java.io.IOException;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

/**
 */
public class UrlContentRewriter implements ContentRewriter {
    private static final Logger log = LoggerFactory.getLogger(UrlContentRewriter.class);
    private PrettyURLEncoderUtil prettyURLEncoderUtil;
    private String key;
    private ContentIdentifierDao contentIdentifierDao;
    @Autowired
    private SiteCache siteCache;

    public UrlContentRewriter() {
        this.key = Aksess.getContextPath() + "/" + Aksess.CONTENT_REQUEST_HANDLER + "?thisId=";
    }

    public String rewriteContent(HttpServletRequest request, String content) {
        if(HttpHelper.isAdminMode(request) || !Aksess.isUrlRewritingEnabled()) {
            return content;
        } else {
            try {
                return rewriteURLs(request, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String rewriteURLs(HttpServletRequest request, String html) throws IOException {
        String contextPath = Aksess.getContextPath();

        CharArrayWriter caw = new CharArrayWriter();

        int prevIndex = 0;
        int index;

        while ((index = html.indexOf(key, prevIndex)) != -1) {
            // Write from start or previous index
            caw.write(html.substring(prevIndex, index));

            char prev = html.charAt(Math.max(0, index - 1));

            String newUrl = null;

            if (index == 0 || prev == '"' || prev == ' ') {
                // Do replace
                StringBuilder id = new StringBuilder();

                int endOfIdIndex = index + key.length();
                char next = html.charAt(endOfIdIndex);
                while (endOfIdIndex < html.length() && next >= '0' && next <= '9') {
                    id.append(next);
                    endOfIdIndex++;
                    if (endOfIdIndex < html.length()) {
                        next = html.charAt(endOfIdIndex);
                    }
                }

                if (id.length() > 0) {
                    try {
                        int thisId = Integer.parseInt(id.toString());
                        // Lookup id and replace with alias
                        int siteId = -1;

                        try {
                            // Get site
                            Content currentPage = tryGetFromRequest(request);
                            if (currentPage != null) {
                                siteId = currentPage.getAssociation().getSiteId();
                            } else {
                                Site site = siteCache.getSiteByHostname(request.getServerName());
                                if (site != null) {
                                    siteId = site.getId();
                                }
                            }

                            // Get alias or url
                            newUrl = contentIdentifierDao.getAliasBySiteIdAndAssociationId(siteId, thisId);
                            if (newUrl == null) {
                                newUrl = prettyURLEncoderUtil.getUrl(thisId);
                            }

                            // Alias found
                            index = endOfIdIndex;

                        } catch (Exception e) {
                            log.error("", e);
                        }

                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                }

            }


            if (newUrl != null) {
                if (contextPath.length() > 0) {
                    caw.write(contextPath);
                }
                // Print new URL - alias
                caw.write(newUrl);

                // Fix remainer of URL - replace & with ?
                if (html.startsWith("&", index)) {
                    caw.write("?");
                    index++;
                    if (html.startsWith("amp;", index)) {
                        index += 4;
                    }
                }

                prevIndex = index;
            } else {
                caw.write(key);
                prevIndex = index + key.length();
            }
        }

        // Write end
        caw.write(html.substring(prevIndex, html.length()));
        return caw.toString();
    }

    public void setPrettyURLEncoderUtil(PrettyURLEncoderUtil prettyURLEncoderUtil) {
        this.prettyURLEncoderUtil = prettyURLEncoderUtil;
    }

    public void setContentIdentifierDao(ContentIdentifierDao contentIdentifierDao) {
        this.contentIdentifierDao = contentIdentifierDao;
    }
}
