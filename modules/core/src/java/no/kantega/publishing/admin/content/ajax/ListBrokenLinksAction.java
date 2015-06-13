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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.modules.linkcheck.check.LinkCheckerJob;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class ListBrokenLinksAction extends SimpleAdminController {
    private final Comparator<LinkOccurrence> comp = new Comparator<LinkOccurrence>() {
        @Override
        public int compare(LinkOccurrence o1, LinkOccurrence o2) {
            return o1.getLastChecked().compareTo(o2.getLastChecked());
        }
    };
    private LinkCheckerJob checker;

    @Autowired
    LinkDao linkDao;

    @Autowired
    private LinkEmitter emitter;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);
        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);
        String sort = params.getString("sort");
        boolean thisPageOnly = params.getBoolean("thisPageOnly", false);

        Map<String, Object> model = new HashMap<>();

        // Extracting currently selected content from it's url
        if (!"".equals(url)) {
            try {
                List<LinkOccurrence> brokenLinks;
                ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);

                if (thisPageOnly) {
                    no.kantega.publishing.common.service.ContentManagementService cms = new no.kantega.publishing.common.service.ContentManagementService(request);
                    linkDao.saveLinksForContent(emitter, cms.getContent(cid, false));
                    checker.executeForContent(cid.getContentId());
                    brokenLinks = linkDao.getBrokenLinksforContentId(cid.getContentId());
                } else {
                    brokenLinks = linkDao.getBrokenLinksUnderParent(cid, sort);
                }
                Date lastChecked = null;
                if (brokenLinks.size() > 0) {
                    lastChecked = Collections.max(brokenLinks, comp).getLastChecked();
                }

                model.put("brokenLinks", brokenLinks);
                model.put("lastChecked", lastChecked == null ? "" : new SimpleDateFormat().format(lastChecked));
                model.put("thisPageOnly", thisPageOnly);
            } catch (ContentNotFoundException e) {
            }
        }
        return new ModelAndView(getView(), model);
    }

    public void setChecker(LinkCheckerJob checker) {
        this.checker = checker;
    }
}
