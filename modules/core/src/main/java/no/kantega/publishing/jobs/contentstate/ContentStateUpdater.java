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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListener;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class ContentStateUpdater {
    private static final Logger log = LoggerFactory.getLogger(ContentStateUpdater.class);

    @Autowired
    private ContentAO contentAO;

    @Resource(name = "contentListenerNotifier")
    private ContentEventListener contentEventListener;

    public void expireContent() {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

        try {
            log.info( "Looking for content that has expired");
            int i = 0;
            while((i = contentAO.getNextExpiredContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = contentAO.getContent(cid, false);
                if (content != null) {
                    ContentVisibilityStatus newVisibilityStatus = ContentVisibilityStatus.EXPIRED;
                    if (content.getExpireAction() == ExpireAction.ARCHIVE) {
                        newVisibilityStatus = ContentVisibilityStatus.ARCHIVED;
                    }
                    log.info("VisibilityStatus of " + content.getTitle() + "(" + content.getId() + ") was set to " + newVisibilityStatus);
                    cms.setContentVisibilityStatus(content, newVisibilityStatus);

                    contentEventListener.contentExpired(new ContentEvent().setContent(content));
                }
            }

            i = 0;
            while((i = contentAO.getNextWaitingContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = contentAO.getContent(cid, false);
                if (content != null) {
                    log.info("VisibilityStatus of " + content.getTitle() + "(" + content.getId() + ") was set to WAITING");
                    cms.setContentVisibilityStatus(content, ContentVisibilityStatus.WAITING);
                }
            }
        } catch (SystemException e) {
            log.error("", e);
        }
        log.info( "Done looking for content that has expired");
    }

    public void publishContent() {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

        try {
            int i = 0;
            log.info( "Looking for content that needs activation");
            while((i = contentAO.getNextActivationContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = contentAO.getContent(cid, true);
                if (content != null) {
                    if (content.getVisibilityStatus() != ContentVisibilityStatus.ACTIVE) {
                        log.info(content.getTitle() + "(" + content.getId() + ") was made visible due to publish date");
                        cms.setContentVisibilityStatus(content, ContentVisibilityStatus.ACTIVE);
                    } else if (content.getStatus() == ContentStatus.PUBLISHED_WAITING) {
                        log.info(content.getTitle() + "(" + content.getId() + ") new version was activated due to change from date");
                        cms.setContentStatus(cid, ContentStatus.PUBLISHED, "");
                    }
                }
            }

        } catch (SystemException | NotAuthorizedException e) {
            log.error("", e);
        }
        log.info( "Done looking for content that needs activation");
    }
}
