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

package no.kantega.publishing.common.util;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.cache.ContentIdentifierCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;
import no.kantega.commons.log.Log;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.CharArrayWriter;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 28, 2008
 * Time: 3:44:24 PM
 */
public class URLRewriter {
    private static final String SOURCE = "aksess.URLRewriter";

    public static String rewriteURLs(ServletRequest request, String html) throws IOException {
        String contextPath = Aksess.getContextPath();
        String key = contextPath + "/content.ap?thisId=";


        CharArrayWriter caw = new CharArrayWriter();

        int prevIndex = 0;
        int index;

        while ((index = html.indexOf(key, prevIndex)) != -1) {
            // Write from start or previous index
            caw.write(html.substring(prevIndex, index));

            char prev = html.charAt(Math.max(0, index - 1));

            String alias = null;

            if (index == 0 || prev == '"' || prev == ' ') {
                // Do replace
                String id = "";

                int endOfIdIndex = index + key.length();
                char next = html.charAt(endOfIdIndex);
                while (endOfIdIndex < html.length() - 1 && next >= '0' && next <= '9') {
                    id += next;
                    endOfIdIndex++;
                    next = html.charAt(endOfIdIndex);
                }

                if (id.length() > 0) {
                    try {
                        int thisId = Integer.parseInt(id);
                        // Lookup id and replace with alias
                        int siteId = -1;

                        try {
                            // Get site
                            Content currentPage = (Content)request.getAttribute("aksess_this");
                            if (currentPage != null) {
                                siteId = currentPage.getAssociation().getSiteId();
                            } else {
                                Site site = SiteCache.getSiteByHostname(request.getServerName());
                                if (site != null) {
                                    siteId = site.getId();
                                }
                            }

                            // Get alias
                            alias = ContentIdentifierCache.getAliasByContentIdentifier(siteId, thisId);

                            if (alias != null) {
                                // Alias found
                                index = endOfIdIndex;
                            }

                        } catch (Exception e) {
                            Log.error(SOURCE, e, null, null);
                        }

                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                }

            }


            if (alias != null) {
                if (contextPath.length() > 0) {
                    caw.write(contextPath);
                }
                // Print new URL - alias
                caw.write(alias);

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

}
