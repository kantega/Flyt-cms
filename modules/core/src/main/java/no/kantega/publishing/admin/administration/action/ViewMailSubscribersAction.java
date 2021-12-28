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

package no.kantega.publishing.admin.administration.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * Controller for viewing all mailsubscriptions.
 */
public class ViewMailSubscribersAction extends AbstractController {
    private String view;
    @Autowired
    private MailSubscriptionService mailSubscriptionService;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");
        String deleteEmail = param.getString("delete");
        if (deleteEmail != null) {
            mailSubscriptionService.removeAllMailSubscriptions(deleteEmail);
        }

        return new ModelAndView(view, Collections.singletonMap("subscriptions", mailSubscriptionService.getAllMailSubscriptions()));
    }

    public void setView(String view) {
        this.view = view;
    }
}
