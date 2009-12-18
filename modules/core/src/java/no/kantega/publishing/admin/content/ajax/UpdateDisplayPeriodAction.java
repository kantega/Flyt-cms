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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import no.kantega.commons.log.Log;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Update display period (publish and expire date) for a page
 */
public class UpdateDisplayPeriodAction implements Controller {
    @Autowired
    private View aksessJsonView;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        RequestParameters param = new RequestParameters(request);
        ContentManagementService cms = new ContentManagementService(request);

        try {
            int associationId = param.getInt("associationId");
            if (associationId != -1) {
                ContentIdentifier cid = new ContentIdentifier();
                cid.setAssociationId(associationId);

                Date publishDate = param.getDateAndTime("from", Aksess.getDefaultDateFormat());
                Date expireDate = param.getDateAndTime("end", Aksess.getDefaultDateFormat());

                boolean updateChildren = param.getBoolean("updateChildren", false);

                if (publishDate != null) {
                    cms.updateDisplayPeriodForContent(cid, publishDate, expireDate, updateChildren);
                }
            }

        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e, null, null);
            model.put("error", Boolean.TRUE);
        }

        return new ModelAndView(aksessJsonView, model);
    }
}
