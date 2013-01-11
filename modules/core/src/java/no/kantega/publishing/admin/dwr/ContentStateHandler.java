/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.dwr;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

import javax.servlet.http.HttpSession;

/**
 * Used by DWR to maintain the state of the user's currentContent session attribute,
 * i.e. the last content viewed by the user.
 *
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 03.jul.2009
 * Time: 09:20:13
 */
@RemoteProxy(name="ContentStateHandler")
public class ContentStateHandler extends AbstractDwrController {


    /**
     * Updates the user's session with the currently viewed content.
     *
     * @param url Url of currently viewed page.
     */
    @RemoteMethod
    public void notifyContentUpdate(String url) {
        HttpSession session = getSession();
        if (session != null) {
            try {
                ContentManagementService cms = new ContentManagementService(getRequest());
                ContentIdentifier cid = ContentIdHelper.fromRequestAndUrl(getRequest(), url);
                Content current = cms.getContent(cid);
                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, current);
            } catch (ContentNotFoundException e) {
                // Do nothing
            } catch (NotAuthorizedException e) {
                Log.error(this.getClass().getName(), e, null, null);
            }
        }
    }

    /**
     * Returns the last viewed content from the user's session.
      * @return associationId
     */
    @RemoteMethod
    public int getCurrentContent() {
        HttpSession session = getSession();
        if (session != null) {
            Content current = (Content) session.getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
            if (current != null) {
                return current.getAssociation().getId();
            }
        }
        return -1;
    }

}
