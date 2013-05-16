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
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;

public class ContentStateUpdater {
    public void expireContent() {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

        try {
            Log.info(this.getClass().getName(), "Looking for content that has expired", null, null);
            int i = 0;
            while((i = ContentAO.getNextExpiredContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = ContentAO.getContent(cid, false);
                if (content != null) {
                    int newVisibilityStatus = ContentVisibilityStatus.EXPIRED;
                    if (content.getExpireAction() == ExpireAction.ARCHIVE) {
                        newVisibilityStatus = ContentVisibilityStatus.ARCHIVED;
                    }
                    cms.setContentVisibilityStatus(content, newVisibilityStatus);

                }
            }

            i = 0;
            while((i = ContentAO.getNextWaitingContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = ContentAO.getContent(cid, false);
                if (content != null) {
                    cms.setContentVisibilityStatus(content, ContentVisibilityStatus.WAITING);
                }
            }
        } catch (SystemException e) {
            Log.error(this.getClass().getName(), e);
        }
    }

    public void publishContent() {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

        try {
            int i = 0;
            Log.info(this.getClass().getName(), "Looking for content that needs activation", null, null);
            while((i = ContentAO.getNextActivationContentId(i)) > 0) {
                ContentIdentifier cid =  ContentIdentifier.fromContentId(i);
                Content content = ContentAO.getContent(cid, true);
                if (content != null) {
                    if (content.getVisibilityStatus() != ContentVisibilityStatus.ACTIVE) {
                        Log.debug(this.getClass().getName(), content.getTitle() + " page was made visible due to publish date", null, null);
                        cms.setContentVisibilityStatus(content, ContentVisibilityStatus.ACTIVE);
                    } else if (content.getStatus() == ContentStatus.PUBLISHED_WAITING) {
                        Log.debug(this.getClass().getName(), content.getTitle() + " new version was activated due to change from date", null, null);
                        cms.setContentStatus(cid, ContentStatus.PUBLISHED, "");
                    }
                }
            }

        } catch (SystemException e) {
            Log.error(this.getClass().getName(), e);
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e);
        }
    }
}
