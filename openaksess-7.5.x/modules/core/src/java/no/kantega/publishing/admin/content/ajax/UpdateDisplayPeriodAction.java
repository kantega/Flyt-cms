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
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.jobs.contentstate.ContentStateUpdater;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Update display period (publish and expire date) for a page
 */
@Controller
public class UpdateDisplayPeriodAction {

    @RequestMapping("/admin/publish/UpdateDisplayPeriod.action")
    public @ResponseBody Map<String, Object> handleRequest(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        RequestParameters param = new RequestParameters(request);
        ContentManagementService cms = new ContentManagementService(request);

        try {
            int associationId = param.getInt("associationId");
            if (associationId != -1) {
                ContentIdentifier cid =  ContentIdentifier.fromAssociationId(associationId);
                ContentIdHelper.assureContentIdAndAssociationIdSet(cid);

                Date publishDate = param.getDateAndTime("from", Aksess.getDefaultDateFormat());
                Date expireDate = param.getDateAndTime("end", Aksess.getDefaultDateFormat());

                boolean updateChildren = param.getBoolean("updateChildren", false);

                if (publishDate != null) {
                    cms.updateDisplayPeriodForContent(cid, publishDate, expireDate, updateChildren);

                    // Update state of content immediately to not confuse user
                    ContentStateUpdater stateUpdater = new ContentStateUpdater();
                    stateUpdater.expireContent();
                    stateUpdater.publishContent();

                    // Update content objects stored in session so user is not confused
                    HttpSession session = request.getSession();
                    Content editedContent = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
                    if (editedContent != null && editedContent.getId() == cid.getContentId()) {
                        editedContent.setPublishDate(publishDate);
                        editedContent.setExpireDate(expireDate);
                    }
                    Content displayedContent = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
                    if (displayedContent != null && displayedContent.getId() == cid.getContentId()) {
                        displayedContent.setPublishDate(publishDate);
                        displayedContent.setExpireDate(expireDate);
                    }
                }
            }

        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e, null, null);
            model.put("error", Boolean.TRUE);
        }

        return model;
    }

}
