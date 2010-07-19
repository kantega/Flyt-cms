/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.ajax;

import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrenceHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ListBrokenLinksAction extends SimpleAdminController {
    @Autowired
    LinkDao linkDao;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        final List<LinkOccurrence> brokenLinks = new ArrayList<LinkOccurrence>();

        String sort = params.getString("sort");

        Map<String, Object> model = new HashMap<String, Object>();

        // Extracting currently selected content from it's url
        ContentIdentifier cid = null;
        if (!"".equals(url)) {
            try {
                cid = new ContentIdentifier(request, url);

                // Find all broken links
                linkDao.doForEachLinkOccurrence(cid, sort, new LinkOccurrenceHandler() {
                    public void handleLinkOccurrence(LinkOccurrence linkOccurrence) {
                        brokenLinks.add(linkOccurrence);
                    }
                });

                model.put("brokenLinks", brokenLinks);
            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(getView(), model);
    }
}
