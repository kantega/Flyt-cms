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

package no.kantega.publishing.jobs.contentstate;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.event.ContentListenerAdapter;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.apache.log4j.Logger;

public class DeleteIfExpiredListener extends ContentListenerAdapter  {

    private static final String SOURCE = "DeleteIfExpiredListener";

    private Logger log = Logger.getLogger(getClass());

    public void contentExpired(Content content) {
        int action = content.getExpireAction();


        if(action == ExpireAction.DELETE) {
            try {
                log.info("Deleting content with id=" + content.getId() +"('" +content.getTitle() +"') because it has expired");
                ContentIdentifier cid = new ContentIdentifier();
                cid.setAssociationId(content.getAssociation().getId());
                EventLog.log("System", null, Event.DELETE_CONTENT_TRASH, content.getTitle(), null);
                ContentAO.deleteContent(cid);
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            } catch (ObjectInUseException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }

}
